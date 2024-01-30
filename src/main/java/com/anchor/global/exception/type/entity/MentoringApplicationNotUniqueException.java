package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class MentoringApplicationNotUniqueException extends ServiceException {

  public MentoringApplicationNotUniqueException(Exception ex) {
    super(ServiceErrorCode.ENTITY_NOT_UNIQUE, ex);
  }

}
