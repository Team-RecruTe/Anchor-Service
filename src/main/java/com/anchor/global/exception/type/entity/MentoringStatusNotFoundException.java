package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class MentoringStatusNotFoundException extends ServiceException {

  public MentoringStatusNotFoundException() {
    super(AnchorErrorCode.MENTORING_STATUS_NOT_FOUND);
  }
}
