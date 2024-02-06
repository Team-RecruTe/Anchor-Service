package com.anchor.global.exception.type.mentoring;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class DuplicateReservedException extends ServiceException {

  public DuplicateReservedException() {
    super(AnchorErrorCode.DUPLICATE_RESERVATION, new SimpleErrorDetail("이미 결제가 완료된 시간대입니다. 다른 시간을 예약해 주세요."));
  }

}
