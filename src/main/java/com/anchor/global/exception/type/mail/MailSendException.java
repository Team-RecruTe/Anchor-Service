package com.anchor.global.exception.type.mail;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.ServiceErrorCode;

public class MailSendException extends ServiceException {

  public MailSendException(Exception ex) {
    super(ServiceErrorCode.MAIL_FAIL, ex);
  }

}
