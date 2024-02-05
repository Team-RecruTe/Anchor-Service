package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ExternalApiException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class InvalidParamException extends ExternalApiException {

  public InvalidParamException(String message) {
    super(AnchorErrorCode.INVALID_PARAM, new SimpleErrorDetail(message));
  }
}
