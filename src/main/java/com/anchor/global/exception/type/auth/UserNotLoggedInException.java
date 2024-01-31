package com.anchor.global.exception.type.auth;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class UserNotLoggedInException extends ServiceException {

  public UserNotLoggedInException() {
    super(ServiceErrorCode.NOT_LOGIN);
  }

}
