package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class MentoringApplicationNotUniqueException extends ServiceException {

  public MentoringApplicationNotUniqueException(Throwable ex) {
    super(AnchorErrorCode.ENTITY_NOT_UNIQUE, ex);
  }

}
