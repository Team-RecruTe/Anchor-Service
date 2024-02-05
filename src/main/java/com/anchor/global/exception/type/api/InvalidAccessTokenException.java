package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class InvalidAccessTokenException extends ExternalApiException {

  public InvalidAccessTokenException(String message) {
    super(AnchorErrorCode.INVALID_ACCESS_TOKEN, new SimpleErrorDetail(message));
  }
}
