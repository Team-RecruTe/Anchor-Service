package com.anchor.global.exception.response;

import com.anchor.global.exception.error.AnchorErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ErrorResponse {

  @JsonIgnore
  private final HttpStatus status;

  private final ErrorCode errorCode;

  protected ErrorResponse(AnchorErrorCode errorCode) {
    this.status = errorCode.getStatus();
    this.errorCode = new ErrorCode(
        errorCode.name(),
        errorCode.getMessage()
    );
  }

  @Getter
  @RequiredArgsConstructor
  static class ErrorCode {

    private final String code;
    private final String message;

  }

}
