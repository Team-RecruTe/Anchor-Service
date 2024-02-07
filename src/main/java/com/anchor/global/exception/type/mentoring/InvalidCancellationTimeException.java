package com.anchor.global.exception.type.mentoring;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class InvalidCancellationTimeException extends ServiceException {

  public InvalidCancellationTimeException() {
    super(AnchorErrorCode.INVALID_CANCELLATION_TIME, new SimpleErrorDetail("멘토링 하루전에는 취소가 불가능합니다."));
  }

}
