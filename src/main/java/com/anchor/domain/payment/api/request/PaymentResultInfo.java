package com.anchor.domain.payment.api.controller.request;

import com.anchor.global.portone.response.SinglePaymentData.PaymentDataDetail;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentResultInfo implements Serializable {


  private String impUid;
  private String merchantUid;
  private Integer amount;

  @Builder
  private PaymentResultInfo(String impUid, String merchantUid, Integer amount) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
  }

  public boolean paymentDataValidation(PaymentDataDetail paymentDataDetail) {
    return impUid.equals(paymentDataDetail.getImpUid())
        && merchantUid.equals(paymentDataDetail.getMerchantUid())
        && amount.equals(paymentDataDetail.getAmount());
  }
}