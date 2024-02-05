package com.anchor.global.exception.type.mentor;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;
import com.anchor.global.exception.response.SimpleErrorDetail;

public class DuplicateEmailException extends ServiceException {

  public DuplicateEmailException() {
    super(AnchorErrorCode.DUPLICATE_COMPANY_EMAIL, new SimpleErrorDetail("이미 등록된 메일입니다. 다시 한번 확인해주세요."));
  }
}
