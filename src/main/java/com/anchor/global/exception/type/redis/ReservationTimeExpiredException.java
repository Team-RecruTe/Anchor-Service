package com.anchor.global.exception.type.redis;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class ReservationTimeExpiredException extends ServiceException {

  public ReservationTimeExpiredException() {
    super(ServiceErrorCode.EXPIRED_TIME, new SimpleErrorDetail("시간내에 예약 갱신을 하지않았습니다. 멘토링 상세내용 페이지로 이동합니다."));
  }

}
