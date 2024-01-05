package com.anchor.domain.payment.api.service.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelData implements Serializable {

  private Integer code;
  private String message;
  private PaymentCancelDetail response;

  @Builder
  private PaymentCancelData(Integer code, String message, PaymentCancelDetail response) {
    this.code = code;
    this.message = message;
    this.response = response;
  }

  public boolean statusCheck() {
    if (code == null) {
      throw new RuntimeException("잘못된 요청입니다.");
    }
    return code.equals(0) && message == null;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class PaymentCancelDetail {

    private Integer amount;

    @JsonProperty("cancel_amount")
    private Integer cancelAmount;

    @JsonProperty("cancel_reason")
    private String cancelReason;

    @Builder
    private PaymentCancelDetail(Integer amount, Integer cancelAmount, String cancelReason) {
      this.amount = amount;
      this.cancelAmount = cancelAmount;
      this.cancelReason = cancelReason;
    }
  }
}
