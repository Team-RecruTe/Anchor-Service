package com.anchor.global.exception.type.api;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class BankNameNotFoundException extends ServiceException {

  public BankNameNotFoundException() {
    super(AnchorErrorCode.BANK_NAME_NOT_FOUND);
  }

}
