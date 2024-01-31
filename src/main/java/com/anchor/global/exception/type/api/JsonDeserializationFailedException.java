package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class JsonDeserializationFailedException extends ServiceException {

  public JsonDeserializationFailedException(Exception ex) {
    super(ServiceErrorCode.DESERIALIZATION_FAIL, ex);
  }

}
