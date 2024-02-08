package com.anchor.global.payment.portone.request;

import com.anchor.global.util.CodeCreator;
import com.anchor.global.util.type.JsonSerializable;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrepareRequestData implements JsonSerializable {

  @JsonProperty("merchant_uid")
  private String merchantUid;

  private Integer amount;

  private PrepareRequestData(String merchantUid, Integer amount) {
    this.merchantUid = merchantUid;
    this.amount = amount;
  }

  public static PrepareRequestData of(String email, Integer amount) {
    String merchantUid = CodeCreator.createMerchantUid(email);
    return new PrepareRequestData(merchantUid, amount);
  }

}
