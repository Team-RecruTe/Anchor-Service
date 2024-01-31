package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class ReviewNotFoundException extends ServiceException {

  public ReviewNotFoundException() {
    super(ServiceErrorCode.ENTITY_NOT_FOUND, new SimpleErrorDetail("리뷰가 존재하지 않습니다."));
  }

}
