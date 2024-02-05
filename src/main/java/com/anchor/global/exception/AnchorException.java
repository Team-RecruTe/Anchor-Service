package com.anchor.global.exception;

import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.ErrorDetail;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class AnchorException extends RuntimeException {

  private AnchorErrorCode errorCode;
  private ErrorDetail details;

  protected AnchorException(AnchorErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  protected AnchorException(AnchorErrorCode errorCode, Throwable ex) {
    super(errorCode.getMessage(), ex);
    this.errorCode = errorCode;
  }

  protected AnchorException(AnchorErrorCode errorCode, ErrorDetail details) {
    super(details.getMessage());
    this.errorCode = errorCode;
    this.details = details;
  }

}
