package com.anchor.global.nhpay.response;

import com.anchor.global.util.type.JsonSerializable;

public interface PayupResult extends JsonSerializable {

  PayupResponseHeader getHeader();

  default boolean validateResponseCode() {
    return getHeader().getResponseCode()
        .equals("00000");
  }

  default String getMessage() {
    return getHeader().getResponseMessage();
  }
}
