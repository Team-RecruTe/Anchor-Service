package com.anchor.domain.payment.api.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenData implements Serializable {

  private Integer code;
  private String message;
  private TokenDataDetail response;

  @Builder
  private TokenData(Integer code, String message, TokenDataDetail response) {
    this.code = code;
    this.message = message;
    this.response = response;
  }

  public boolean statusCheck() {
    if (code == null) {
      throw new RuntimeException("잘못된 요청입니다.");
    }
    return code.equals(0) && message == null;
  }

  @Getter
  @NoArgsConstructor
  public static class TokenDataDetail {

    @JsonProperty("access_token")
    private String accessToken;

    private Long now;

    @JsonProperty("expired_at")
    private Long expiredAt;

    @Builder
    private TokenDataDetail(String accessToken, Long now, Long expiredAt) {
      this.accessToken = accessToken;
      this.now = now;
      this.expiredAt = expiredAt;
    }
  }
}
