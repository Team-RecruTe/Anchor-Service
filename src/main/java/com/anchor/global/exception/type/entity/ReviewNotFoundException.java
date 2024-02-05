package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class ReviewNotFoundException extends ServiceException {

  public ReviewNotFoundException() {
    super(AnchorErrorCode.ENTITY_NOT_FOUND, new SimpleErrorDetail("리뷰가 존재하지 않습니다."));
  }

}
