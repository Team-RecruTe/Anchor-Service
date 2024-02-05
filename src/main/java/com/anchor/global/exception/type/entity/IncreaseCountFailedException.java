package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class IncreaseCountFailedException extends ServiceException {

  public IncreaseCountFailedException(Throwable ex) {
    super(AnchorErrorCode.INCREASE_FAILED, ex);
  }
}
