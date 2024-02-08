package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class CareerNotFoundException extends ServiceException {

  public CareerNotFoundException() {
    super(AnchorErrorCode.CAREER_NOT_FOUND);
  }
}
