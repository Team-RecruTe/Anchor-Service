package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class MentoringNotFoundException extends ServiceException {

  public MentoringNotFoundException() {
    super(ServiceErrorCode.ENTITY_NOT_FOUND, new SimpleErrorDetail("조건에 부합하는 멘토링정보가 존재하지 않습니다."));
  }

}
