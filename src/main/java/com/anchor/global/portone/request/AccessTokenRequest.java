package com.anchor.global.portone.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class AccessTokenRequest implements Serializable {

  @JsonProperty("imp_key")
  private String impKey;
  @JsonProperty("imp_secret")
  private String impSecret;

  public AccessTokenRequest(String impKey, String impSecret) {
    this.impKey = impKey;
    this.impSecret = impSecret;
  }
}