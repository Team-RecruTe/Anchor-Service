package com.anchor.domain.user.api.service;

import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.UserImageRequest;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.portone.request.RequiredPaymentCancelData;
import com.anchor.global.portone.response.PaymentCancelResult;
import com.anchor.global.portone.response.PaymentResult;
import com.anchor.global.util.PaymentUtils;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final MentoringReviewRepository mentoringReviewRepository;
  private final PayupRepository payupRepository;
  private final PaymentUtils paymentUtils;

  public void writeReview(Long id, MentoringReviewInfo mentoringReviewInfo) {
    Optional<MentoringApplication> mentoringApplication = mentoringApplicationRepository.findById(id);
    MentoringReview dbMentoringReviewInsert = MentoringReview.builder()
        .contents(mentoringReviewInfo.getContents())
        .mentoringApplication(mentoringApplication.get())
        .build();
    mentoringReviewRepository.save(dbMentoringReviewInsert);
  }

  @Transactional
  public UserInfoResponse getProfile(String email){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    return new UserInfoResponse(user);
  }

  @Transactional
  public void editNickname(String email, UserNicknameRequest userNicknameRequest){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    user.editNickname(userNicknameRequest);
    userRepository.save(user);
  }

  @Transactional
  public void uploadImage(String email, UserImageRequest userImageRequest){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    user.uploadImage(userImageRequest);
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(String email){
    User user = userRepository.findByEmail(email)
        .orElseThrow(()->{
          return new RuntimeException("해당 유저를 찾을 수 없습니다.");
        });
    userRepository.delete(user);
  }

  @Transactional(readOnly = true)
  public Page<AppliedMentoringInfo> loadAppliedMentoringList(SessionUser sessionUser, Pageable pageable) {
    User user = getUser(sessionUser);
    return mentoringApplicationRepository.findByUserId(user.getId(), pageable);
  }

  @Transactional
  public boolean changeAppliedMentoringStatus(SessionUser sessionUser, MentoringStatusInfo changeRequest) {
    User user = getUser(sessionUser);
    List<RequiredMentoringStatusInfo> mentoringStatusList = changeRequest.getRequiredMentoringStatusInfos();
    mentoringStatusList.forEach(status -> {
      try {
        validateMentoringStatus(status);
        changeStatus(user, status);
      } catch (NoSuchElementException | IllegalArgumentException e) {
        log.warn(e.getMessage());
      }
    });

    return true;
  }

  private void validateMentoringStatus(RequiredMentoringStatusInfo statusInfo) {
    MentoringStatus status = statusInfo.getMentoringStatus();
    if (status.equals(MentoringStatus.WAITING) || status.equals(MentoringStatus.APPROVAL)) {
      throw new IllegalArgumentException("변경하려는 상태가 'CANCELED' 또는 'COMPLETE'가 아닙니다.");
    }
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private void changeStatus(User user, RequiredMentoringStatusInfo mentoringStatusInfo) {
    DateTimeRange dateTimeRange = mentoringStatusInfo.getMentoringReservedTime();
    LocalDateTime startDateTime = dateTimeRange.getFrom();
    LocalDateTime endDateTime = dateTimeRange.getTo();
    Long userId = user.getId();

    MentoringApplication mentoringApplication =
        mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(startDateTime, endDateTime, userId)
            .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 존재하지 않습니다."));
    mentoringApplication.changeStatus(mentoringStatusInfo.getMentoringStatus());
    processMentoringStatus(mentoringApplication);
    mentoringApplicationRepository.save(mentoringApplication);
  }

  private void processMentoringStatus(MentoringApplication mentoringApplication) {
    MentoringStatus mentoringStatus = mentoringApplication.getMentoringStatus();
    if (mentoringStatus.equals(MentoringStatus.CANCELLED)) {
      cancelPaymentIfCancelled(mentoringApplication);
    }
    if (mentoringStatus.equals(MentoringStatus.COMPLETE)) {
      savePayup(mentoringApplication);
    }
  }

  private void cancelPaymentIfCancelled(MentoringApplication application) {
    MentoringStatus status = application.getMentoringStatus();
    Payment payment = application.getPayment();
    RequiredPaymentCancelData requiredPaymentCancelData = new RequiredPaymentCancelData(payment);
    Optional<PaymentResult> paymentCancelResult = paymentUtils.request(status, requiredPaymentCancelData);
    paymentCancelResult.ifPresent(result -> payment.editPaymentCancelStatus((PaymentCancelResult) result));
  }

  private void savePayup(MentoringApplication application) {
    Mentoring mentoring = application.getMentoring();
    Mentor mentor = mentoring.getMentor();
    Payment payment = application.getPayment();
    Payup payup = Payup.builder()
        .mentor(mentor)
        .payment(payment)
        .build();
    payupRepository.save(payup);
  }

}
