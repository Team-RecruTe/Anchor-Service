package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class InvalidAccessTokenException extends ServiceException {

  public InvalidAccessTokenException(String message) {
    super(ServiceErrorCode.INVALID_ACCESS_TOKEN, new SimpleErrorDetail(message));
  }
}
