package com.anchor.global.exception.type.auth;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class AttributeNotFoundException extends ServiceException {

  public AttributeNotFoundException(Exception ex) {
    super(ServiceErrorCode.ATTRIBUTE_NOT_FOUND, ex);
  }

}
