package com.anchor.global.exception;

import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.ErrorDetail;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ServiceException extends AnchorException {

  protected ServiceException(AnchorErrorCode errorCode) {
    super(errorCode);
  }

  protected ServiceException(AnchorErrorCode errorCode, Throwable ex) {
    super(errorCode, ex);
  }

  protected ServiceException(AnchorErrorCode errorCode, ErrorDetail details) {
    super(errorCode, details);
  }

}
