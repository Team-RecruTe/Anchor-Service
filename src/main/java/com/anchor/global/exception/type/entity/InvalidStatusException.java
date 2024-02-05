package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class InvalidStatusException extends ServiceException {

  public InvalidStatusException() {
    super(AnchorErrorCode.INVALID_STATUS);
  }

}
