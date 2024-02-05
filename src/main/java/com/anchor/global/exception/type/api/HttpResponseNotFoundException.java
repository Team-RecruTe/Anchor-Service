package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class HttpResponseNotFoundException extends ExternalApiException {

  public HttpResponseNotFoundException(Throwable ex) {
    super(AnchorErrorCode.RESPONSE_NOT_FOUND, ex);
  }

}
