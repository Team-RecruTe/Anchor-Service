package com.anchor.global.exception.type.json;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class JsonSerializationFailedException extends ServiceException {

  public JsonSerializationFailedException(Exception ex) {
    super(AnchorErrorCode.SERIALRIZATION_FAIL, ex);
  }

}
