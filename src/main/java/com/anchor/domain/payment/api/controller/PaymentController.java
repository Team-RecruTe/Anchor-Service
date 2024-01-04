package com.anchor.domain.payment.api.controller;

import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.controller.request.RequiredPaymentInfo;
import com.anchor.domain.payment.api.service.PaymentService;
import com.anchor.domain.payment.api.service.response.PaidMentoringInfo;
import com.anchor.domain.payment.api.service.response.PaymentValidationInfo;
import com.anchor.global.auth.SessionUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpSession;
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
  public ResponseEntity<PaymentValidationInfo> paymentValidation(@RequestBody PaymentResultInfo paymentResultInfo)
      throws JsonProcessingException {
    String validationResult = paymentService.validatePaymentResult(paymentResultInfo);

    PaymentValidationInfo paymentValidationInfo = new PaymentValidationInfo(paymentResultInfo, validationResult);

    if (validationResult.equals("success")) {

      return ResponseEntity.ok()
          .body(paymentValidationInfo);
    } else {

      return ResponseEntity.badRequest()
          .build();
    }
  }

  /**
   * 결제내역을 저장합니다.
   */
  @PostMapping
  public ResponseEntity<PaidMentoringInfo> savePaymentData(@RequestBody RequiredPaymentInfo requiredPaymentInfo,
      HttpSession session) {
    SessionUser sessionUser = getSessionUserFromSession(session);

    PaidMentoringInfo paidMentoringInfo = paymentService.savePayment(requiredPaymentInfo, sessionUser);

    if (paidMentoringInfo == null) {

      return ResponseEntity.badRequest()
          .build();
    } else {

      return ResponseEntity.ok()
          .body(paidMentoringInfo);

    }
  }

  private SessionUser getSessionUserFromSession(HttpSession session) {

    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 없습니다. 잘못된 접근입니다.");
    }
    return sessionUser;
  }
}
