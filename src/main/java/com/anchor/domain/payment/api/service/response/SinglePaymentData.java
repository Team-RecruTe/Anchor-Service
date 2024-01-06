package com.anchor.domain.payment.api.service.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SinglePaymentData implements Serializable {

  private Integer code;
  private String message;
  private PaymentDataDetail response;

  @Builder
  private SinglePaymentData(Integer code, String message, PaymentDataDetail response) {
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
  public static class PaymentDataDetail {

    private int amount;

    @JsonProperty("imp_uid")
    private String impUid;

    @JsonProperty("merchant_uid")
    private String merchantUid;

    @Builder
    private PaymentDataDetail(int amount, String impUid, String merchantUid) {
      this.amount = amount;
      this.impUid = impUid;
      this.merchantUid = merchantUid;
    }
  }
}
