package com.anchor.global.payment.portone.response;

import com.anchor.global.util.type.JsonSerializable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessTokenResult implements JsonSerializable {

  @JsonProperty("access_token")
  private String accessToken;

  @Builder
  private AccessTokenResult(String accessToken, Long now, Long expiredAt) {
    this.accessToken = accessToken;
  }

}