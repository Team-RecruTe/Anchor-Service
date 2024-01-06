package com.anchor.domain.payment.api.service;

import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.service.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.global.util.ExternalApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private static final String SUCCESS = "success";
  private static final String FAIL = "fail";

  private final ExternalApiUtil apiUtil;

  public String validatePaymentResult(PaymentResultInfo paymentResultInfo) {

    PaymentDataDetail paymentDataDetail = apiUtil.getPaymentDataDetail(paymentResultInfo);

    return paymentResultInfo.paymentDataValidation(paymentDataDetail) ? SUCCESS : FAIL;
  }

}
