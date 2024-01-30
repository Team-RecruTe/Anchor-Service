package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class JsonSerializationFailedException extends ServiceException {

  public JsonSerializationFailedException(Exception ex) {
    super(ServiceErrorCode.DESERIALIZATION_FAIL, ex);
  }

}
