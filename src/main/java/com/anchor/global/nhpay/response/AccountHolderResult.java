package com.anchor.global.nhpay.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountHolderResult implements PayupResult {

  @JsonProperty("Header")
  private PayupResponseHeader header;
  @JsonProperty("Bncd")
  private String bankCode;
  @JsonProperty("Acno")
  private String accountNumber;
  @JsonProperty("Dpnm")
  private String accountName;

  public boolean isSameAs(String accountName) {
    return this.accountName.equals(accountName);
  }
}
