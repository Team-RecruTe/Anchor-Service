package com.anchor.global.exception.type.mentor;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class FutureDateException extends ServiceException {

  public FutureDateException() {
    super(ServiceErrorCode.FUTURE_DATE, new SimpleErrorDetail("당월 기준 미래시점의 정산내역은 조회가 불가능합니다."));
  }
}
