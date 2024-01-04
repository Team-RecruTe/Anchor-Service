package com.anchor.domain.payment.api.controller.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class TokenRequest implements Serializable {

  @JsonProperty("imp_key")
  private String impKey;
  @JsonProperty("imp_secret")
  private String impSecret;

  public TokenRequest(String impKey, String impSecret) {
    this.impKey = impKey;
    this.impSecret = impSecret;
  }
}
