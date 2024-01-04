package com.anchor.domain.payment.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentNumberInfo {

  @JsonProperty("imp_uid")
  private String impUid;
  @JsonProperty("merchant_uid")
  private String merchantUid;
}
