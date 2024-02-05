package com.anchor.global.payment.portone.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCancelResult implements PaymentResult {

  private Integer amount;

  @JsonProperty("cancel_amount")
  private Integer cancelAmount;


  @Builder
  private PaymentCancelResult(Integer amount, Integer cancelAmount) {
    this.amount = amount;
    this.cancelAmount = cancelAmount;
  }

}