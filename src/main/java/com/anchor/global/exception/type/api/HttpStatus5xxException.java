package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class HttpStatus5xxException extends ExternalApiException {

  public HttpStatus5xxException() {
    super(AnchorErrorCode.RESPONSE_NOT_FOUND);
  }

}
