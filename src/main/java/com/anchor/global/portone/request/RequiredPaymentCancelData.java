package com.anchor.global.portone.request;

import com.anchor.domain.payment.domain.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredPaymentCancelData implements RequiredPaymentData {

  private String impUid;
  private String merchantUid;
  private Integer amount;
  @JsonIgnore
  private Integer cancelAmount;
  private Integer checkSum;

  public RequiredPaymentCancelData(Payment payment) {
    this.impUid = payment.getImpUid();
    this.merchantUid = payment.getMerchantUid();
    this.amount = payment.getAmount();
    this.cancelAmount = payment.getAmount();
    this.checkSum = payment.getAmount();
  }

}