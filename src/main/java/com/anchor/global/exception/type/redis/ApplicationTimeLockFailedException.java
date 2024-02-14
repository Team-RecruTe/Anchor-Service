package com.anchor.global.exception.type.redis;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class ApplicationTimeLockFailedException extends ServiceException {

  public ApplicationTimeLockFailedException(Throwable ex) {
    super(AnchorErrorCode.LOCK_ACQUIRE_FAIL, ex);
  }
}
