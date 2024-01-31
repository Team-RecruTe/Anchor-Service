package com.anchor.global.exception.type.json;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class JsonDeserializationFailedException extends ServiceException {

  public JsonDeserializationFailedException(Throwable ex) {
    super(AnchorErrorCode.DESERIALIZATION_FAIL, ex);
  }

}
