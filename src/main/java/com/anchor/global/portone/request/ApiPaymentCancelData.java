package com.anchor.global.portone.request;

import com.anchor.domain.payment.domain.Payment;
import com.anchor.global.portone.response.PaymentCancelData.PaymentCancelDetail;
import com.anchor.global.portone.response.SinglePaymentData.PaymentDataDetail;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiPaymentCancelData implements Serializable {

  private String impUid;
  private String merchantUid;
  private Integer amount;
  @JsonIgnore
  private Integer cancelAmount;
  private Integer checkSum;

  public ApiPaymentCancelData(Payment payment) {
    this.impUid = payment.getImpUid();
    this.merchantUid = payment.getMerchantUid();
    this.amount = payment.getAmount();
    this.cancelAmount = payment.getAmount();
    this.checkSum = payment.getAmount();
  }

  public boolean paymentDataValidation(PaymentDataDetail paymentDataDetail) {
    return impUid.equals(paymentDataDetail.getImpUid())
        && merchantUid.equals(paymentDataDetail.getMerchantUid())
        && amount.equals(paymentDataDetail.getAmount());
  }

  public boolean cancelResultValidation(PaymentCancelDetail cancelDetail) {
    return this.cancelAmount.equals(cancelDetail.getCancelAmount())
        && this.amount.equals(cancelDetail.getAmount());
  }


}