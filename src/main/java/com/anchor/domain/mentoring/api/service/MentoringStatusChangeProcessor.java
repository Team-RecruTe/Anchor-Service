package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.global.exception.type.entity.InvalidStatusException;
import com.anchor.global.exception.type.mentoring.InvalidCancellationTimeException;
import com.anchor.global.payment.portone.request.RequiredPaymentCancelData;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.redis.lock.RedisLockFacade;
import com.anchor.global.util.PaymentClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MentoringStatusChangeProcessor {

  private static final int MAX_DATE_FOR_CANCELLATION = 1;

  private final PaymentClient paymentClient;
  private final RedisLockFacade redisLockFacade;
  private final PayupRepository payupRepository;

  public void changeStatusProcess(MentoringApplication application, MentoringStatus changeRequestStatus,
      LocalDateTime requestTime) {

    switch (changeRequestStatus) {
      case CANCELLED -> {
        validateCancelRequestTime(application, requestTime);
        cancelPayment(application, changeRequestStatus);
      }
      case COMPLETE -> savePayup(application);
      case APPROVAL -> {
      }
      case WAITING -> throw new InvalidStatusException();
    }
    application.changeStatus(changeRequestStatus);
  }

  private void validateCancelRequestTime(MentoringApplication application, LocalDateTime requestTime) {
    LocalDate requestDate = requestTime.toLocalDate();
    LocalDate applicationStartDate = application.getStartDateTime()
        .toLocalDate();
    Period period = requestDate.until(applicationStartDate);
    int dateInterval = period.getDays();
    if (dateInterval <= MAX_DATE_FOR_CANCELLATION) {
      throw new InvalidCancellationTimeException();
    }
  }

  private void cancelPayment(MentoringApplication application, MentoringStatus mentoringStatus) {
    Payment payment = application.getPayment();
    RequiredPaymentCancelData requiredPaymentCancelData = new RequiredPaymentCancelData(payment);
    Optional<PaymentResult> paymentCancelResult = paymentClient.request(mentoringStatus, requiredPaymentCancelData);
    paymentCancelResult.ifPresent(result -> payment.editPaymentCancelStatus((PaymentCancelResult) result));
  }

  private void savePayup(MentoringApplication application) {
    Long mentoringId = application.getMentoring()
        .getId();
    Mentoring mentoring = redisLockFacade.increaseTotalApplication(mentoringId);
    Mentor mentor = mentoring.getMentor();
    Payment payment = application.getPayment();
    Payup payup = Payup.builder()
        .mentor(mentor)
        .payment(payment)
        .build();
    payupRepository.save(payup);
  }

}
