package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class InvalidStatusException extends ServiceException {

  public InvalidStatusException() {
    super(ServiceErrorCode.INVALID_STATUS);
  }

}
