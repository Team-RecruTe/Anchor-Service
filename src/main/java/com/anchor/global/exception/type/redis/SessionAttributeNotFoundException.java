package com.anchor.global.exception.type.redis;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class SessionAttributeNotFoundException extends ServiceException {

  public SessionAttributeNotFoundException() {
    super(AnchorErrorCode.SESSION_ATTRIBUTE_NOT_FOUND);
  }

}
