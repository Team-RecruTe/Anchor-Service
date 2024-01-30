package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class InvalidImpUidException extends ServiceException {

  public InvalidImpUidException(String message) {
    super(ServiceErrorCode.INVALID_IMP_UID, new SimpleErrorDetail(message));
  }
}
