package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.service.response.PaymentCompleteResult;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.global.payment.portone.request.RequiredPaymentCreateData;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.payment.portone.response.SinglePaymentResult;
import com.anchor.global.util.PaymentClient;
import com.anchor.global.util.ResponseType;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentClient paymentClient;
  private final PaymentRepository paymentRepository;

  public ResponseType validatePaymentResult(PaymentResultInfo paymentResultInfo) {
    RequiredPaymentCreateData requiredPaymentCreateData = new RequiredPaymentCreateData(paymentResultInfo.getImpUid());
    Optional<PaymentResult> paymentSelectResult = paymentClient.request(MentoringStatus.WAITING,
        requiredPaymentCreateData);
    SinglePaymentResult result = (SinglePaymentResult) paymentSelectResult.orElseThrow(
        () -> new NoSuchElementException("PaymentSelectResult 값이 존재하지 않습니다."));
    return ResponseType.of(paymentResultInfo.isSameAs(result));
  }

  public PaymentCompleteResult getPaymentResult(String orderUid) {
    return paymentRepository.findCompletedPaymentByOrderUid(orderUid);
  }

}
