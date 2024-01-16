package com.anchor.global.portone.request;

import com.anchor.domain.payment.domain.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredPaymentCancelData implements RequiredPaymentData {

  @JsonProperty("imp_uid")
  private String impUid;
  @JsonProperty("merchant_uid")
  private String merchantUid;
  private Integer amount;
  @JsonIgnore
  private Integer cancelAmount;
  @JsonProperty("check_sum")
  private Integer checkSum;

  public RequiredPaymentCancelData(Payment payment) {
    this.impUid = payment.getImpUid();
    this.merchantUid = payment.getMerchantUid();
    this.amount = payment.getAmount();
    this.cancelAmount = payment.getAmount();
    this.checkSum = payment.getAmount();
  }

}