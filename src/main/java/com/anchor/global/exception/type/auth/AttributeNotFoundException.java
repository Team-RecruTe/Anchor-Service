package com.anchor.global.exception.type.auth;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class AttributeNotFoundException extends ServiceException {

  public AttributeNotFoundException(Throwable ex) {
    super(AnchorErrorCode.ATTRIBUTE_NOT_FOUND, ex);
  }

}
