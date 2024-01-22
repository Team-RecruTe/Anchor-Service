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
public class AccessTokenResult implements Serializable {

  @JsonProperty("access_token")
  private String accessToken;

  @Builder
  private AccessTokenResult(String accessToken, Long now, Long expiredAt) {
    this.accessToken = accessToken;
  }

}