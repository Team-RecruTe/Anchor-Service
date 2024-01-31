package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class MentoringApplicationNotFoundException extends ServiceException {

  public MentoringApplicationNotFoundException() {
    super(AnchorErrorCode.ENTITY_NOT_FOUND, new SimpleErrorDetail("조건에 부합하는 멘토링 신청내역이 존재하지 않습니다."));
  }

}
