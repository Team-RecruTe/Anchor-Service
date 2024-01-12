package com.anchor.domain.user.api.service;

import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.portone.response.PaymentCancelData.PaymentCancelDetail;
import com.anchor.global.util.ExternalApiUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final PaymentRepository paymentRepository;
  private final ExternalApiUtil apiUtil;
  private final MentoringReviewRepository mentoringReviewRepository;

  public void writeReview(Long id, MentoringReviewInfo mentoringReviewInfo) {
    Optional<MentoringApplication> mentoringApplication = mentoringApplicationRepository.findById(id);
    MentoringReview dbMentoringReviewInsert = MentoringReview.builder()
        .contents(mentoringReviewInfo.getContents())
        .mentoringApplication(mentoringApplication.get())
        .build();
    mentoringReviewRepository.save(dbMentoringReviewInsert);
  }

  @Transactional(readOnly = true)
  public List<AppliedMentoringInfo> loadAppliedMentoringList(SessionUser sessionUser) {

    User user = getUser(sessionUser);

    List<MentoringApplication> mentoringApplicationList = user.getMentoringApplicationList();

    return mentoringApplicationList.isEmpty() ?
        null :
        mentoringApplicationList
            .stream()
            .map(AppliedMentoringInfo::new)
            .toList();
  }

  @Transactional
  public boolean changeAppliedMentoringStatus(SessionUser sessionUser, MentoringStatusInfo changeRequest) {
    User user = getUser(sessionUser);

    List<RequiredMentoringStatusInfo> mentoringStatusList = changeRequest.getMentoringStatusList();
    mentoringStatusList.forEach(status -> {
      try {

        if (status.mentoringStatusIsCanceledOrComplete()) {
          changeStatus(user, status);
        }

      } catch (NoSuchElementException | IllegalArgumentException e) {
        log.warn(e.getMessage());
      }
    });

    return true;
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private void changeStatus(User user, RequiredMentoringStatusInfo mentoringStatusInfo) {
    LocalDateTime startDateTime = mentoringStatusInfo.getStartDateTime();
    LocalDateTime endDateTime = mentoringStatusInfo.getEndDateTime();
    Long userId = user.getId();

    MentoringApplication mentoringApplication =
        mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(startDateTime, endDateTime, userId)
            .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 존재하지 않습니다."));

    mentoringApplication.changeStatus(mentoringStatusInfo.getStatus());

    mentoringApplicationRepository.save(mentoringApplication);

    MentoringStatus mentoringStatus = mentoringApplication.getMentoringStatus();

    if (mentoringStatus.equals(MentoringStatus.CANCELLED)) {
      //상태가 캔슬일 시, 캔슬 작업 진행
      paymentCancelProcess(mentoringApplication);
    }
  }

  private void paymentCancelProcess(MentoringApplication mentoringApplication) {
    Payment payment = mentoringApplication.getPayment();
    if (payment.isCancelled()) {
      throw new RuntimeException("이미 취소된 결제내역입니다.");
    }
    PaymentCancelDetail paymentCancelDetail = apiUtil.paymentCancel(payment);

    payment.editPaymentCancelStatus(paymentCancelDetail);

    paymentRepository.save(payment);
  }

}
