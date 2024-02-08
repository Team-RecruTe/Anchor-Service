package com.anchor.global.payment.portone.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrepareResult implements PaymentResult {

  @JsonProperty("merchant_uid")
  private String merchantUid;

}
