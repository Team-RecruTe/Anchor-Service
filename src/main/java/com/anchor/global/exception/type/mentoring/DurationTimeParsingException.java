package com.anchor.global.exception.type.mentoring;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class DurationTimeParsingException extends ServiceException {

  public DurationTimeParsingException() {
    super(AnchorErrorCode.INVALID_DURATION_TIME);
  }
}
