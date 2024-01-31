package com.anchor.global.exception.type.entity;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class UserNotFoundException extends ServiceException {

  public UserNotFoundException() {
    super(ServiceErrorCode.ENTITY_NOT_FOUND, new SimpleErrorDetail("조건에 해당하는 회원정보가 존재하지 않습니다."));
  }

}
