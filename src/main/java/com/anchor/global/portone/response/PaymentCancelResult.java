package com.anchor.global.portone.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCancelResult implements Serializable, PaymentResult {

  private Integer amount;

  @JsonProperty("cancel_amount")
  private Integer cancelAmount;


  @Builder
  private PaymentCancelResult(Integer amount, Integer cancelAmount) {
    this.amount = amount;
    this.cancelAmount = cancelAmount;
  }

}