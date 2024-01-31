package com.anchor.global.exception.type.mail;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class MailSendException extends ServiceException {

  public MailSendException(Throwable ex) {
    super(AnchorErrorCode.MAIL_FAIL, ex);
  }

}
