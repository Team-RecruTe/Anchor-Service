package com.anchor.global.exception.type.auth;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class UserNotLoggedInException extends ServiceException {

  public UserNotLoggedInException() {
    super(AnchorErrorCode.NOT_LOGIN);
  }

}
