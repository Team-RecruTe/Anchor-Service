package com.anchor.global.exception.type.redis;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class LockAcquisitionFailedException extends ServiceException {

  public LockAcquisitionFailedException() {
    super(ServiceErrorCode.LOCK_ACQUIRE_FAIL);
  }

}
