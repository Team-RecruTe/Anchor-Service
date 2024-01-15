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
public class SinglePaymentResult implements Serializable, PaymentResult {

  private int amount;

  @JsonProperty("imp_uid")
  private String impUid;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  @Builder
  private SinglePaymentResult(int amount, String impUid, String merchantUid) {
    this.amount = amount;
    this.impUid = impUid;
    this.merchantUid = merchantUid;
  }

}