package com.anchor.domain.payment.api.controller;

import com.anchor.domain.payment.api.service.PaymentService;
import com.anchor.domain.payment.api.service.response.PaymentCompleteResult;
import com.anchor.global.util.view.ViewResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentViewController {

  private final PaymentService paymentService;
  private final ViewResolver viewResolver;

  /**
   * 결제 완료 페이지를 조회합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @GetMapping("/complete")
  public String viewPaymentCompletePage(@RequestParam("order") String orderUid, Model model) {
    PaymentCompleteResult paymentResult = paymentService.getPaymentResult(orderUid);
    model.addAttribute("paymentResult", paymentResult);
    return viewResolver.getViewPath("payment", "complete");
  }

}