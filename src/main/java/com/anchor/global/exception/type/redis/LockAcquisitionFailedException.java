package com.anchor.global.exception.type.redis;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class LockAcquisitionFailedException extends ServiceException {

  public LockAcquisitionFailedException() {
    super(AnchorErrorCode.LOCK_ACQUIRE_FAIL);
  }

}
