package com.anchor.global.payment.portone.request;


import com.anchor.global.util.type.JsonSerializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessTokenRequest implements JsonSerializable {

  @JsonProperty("imp_key")
  private String impKey;
  @JsonProperty("imp_secret")
  private String impSecret;

  public AccessTokenRequest(String impKey, String impSecret) {
    this.impKey = impKey;
    this.impSecret = impSecret;
  }
}