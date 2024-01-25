package com.anchor.domain.user.api.service;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.domain.user.api.controller.request.MentoringReservedTime;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.RequiredEditReview;
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

  @Transactional
  public void writeReview(SessionUser sessionUser, MentoringReviewInfo mentoringReviewInfo) {
    MentoringApplication mentoringApplication = getMentoringApplicationByTimeRange(sessionUser.getId(),
        mentoringReviewInfo.getTimeRange()
            .getFrom(), mentoringReviewInfo.getTimeRange()
            .getTo());
    MentoringReview dbMentoringReviewInsert = MentoringReview.builder()
        .ratings(mentoringReviewInfo.getRatings())
        .contents(mentoringReviewInfo.getContents())
        .mentoringApplication(mentoringApplication)
        .build();
    mentoringApplication.completedReview();
    mentoringReviewRepository.save(dbMentoringReviewInsert);
  }

  @Transactional(readOnly = true)
  public RequiredEditReview getReview(SessionUser sessionUser, MentoringReservedTime reservedTime) {
    DateTimeRange dateTimeRange = DateTimeRange.of(reservedTime.getStartTime(), reservedTime.getEndTime());
    MentoringReview mentoringReview = mentoringReviewRepository.findByTimeRange(dateTimeRange, sessionUser.getId());
    return RequiredEditReview.of(mentoringReview);
  }

  @Transactional
  public void editReview(RequiredEditReview editReview) {
    MentoringReview mentoringReview = mentoringReviewRepository.findById(editReview.getId())
        .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
    mentoringReview.editReview(editReview);
    mentoringReviewRepository.save(mentoringReview);
  }

  @Transactional(readOnly = true)
  public UserInfoResponse getProfile(SessionUser sessionUser) {
    User user = getUser(sessionUser);
    return new UserInfoResponse(user);
  }

  @Transactional
  public void editNickname(SessionUser sessionUser, UserNicknameRequest userNicknameRequest) {
    User user = getUser(sessionUser);
    user.editNickname(userNicknameRequest);
    userRepository.save(user);
  }

  @Transactional
  public void uploadImage(SessionUser sessionUser, UserImageRequest userImageRequest) {
    User user = getUser(sessionUser);
    user.uploadImage(userImageRequest);
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(SessionUser sessionUser) {
    User user = getUser(sessionUser);
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

  private MentoringApplication getMentoringApplicationByTimeRange(Long userId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(
            startDateTime, endDateTime, userId)
        .orElseThrow(() -> new RuntimeException("신청내역이 존재하지 않습니다."));
  }

  private void changeStatus(User user, RequiredMentoringStatusInfo mentoringStatusInfo) {
    DateTimeRange dateTimeRange = mentoringStatusInfo.getMentoringReservedTime();
    MentoringApplication mentoringApplication = getMentoringApplicationByTimeRange(user.getId(),
        dateTimeRange.getFrom(), dateTimeRange.getTo());
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
