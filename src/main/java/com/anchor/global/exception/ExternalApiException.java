package com.anchor.global.exception;

import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.ErrorDetail;

public abstract class ExternalApiException extends AnchorException {

  protected ExternalApiException(AnchorErrorCode errorCode) {
    super(errorCode);
  }

  protected ExternalApiException(AnchorErrorCode errorCode, Throwable ex) {
    super(errorCode, ex);
  }

  protected ExternalApiException(AnchorErrorCode errorCode, ErrorDetail details) {
    super(errorCode, details);
  }

}
