package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class HttpClientException extends ServiceException {

  public HttpClientException(Exception ex) {
    super(ServiceErrorCode.HTTP_ERROR, ex);
  }
}
