package com.anchor.domain.payment.api.controller;

import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.service.PaymentService;
import com.anchor.domain.payment.api.service.response.PaymentValidationInfo;
import com.anchor.global.util.ResponseType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

  private final PaymentService paymentService;

  /**
   * 결제 금액을 검증합니다.
   */
  @PostMapping("/validation")
  public ResponseEntity<PaymentValidationInfo> paymentValidation(@RequestBody PaymentResultInfo paymentResultInfo) {
    ResponseType validationResult = paymentService.validatePaymentResult(paymentResultInfo);
    PaymentValidationInfo paymentValidationInfo = new PaymentValidationInfo(paymentResultInfo, validationResult);
    if (validationResult.equals(ResponseType.SUCCESS)) {
      return ResponseEntity.ok()
          .body(paymentValidationInfo);
    } else {
      return ResponseEntity.badRequest()
          .build();
    }
  }
}
