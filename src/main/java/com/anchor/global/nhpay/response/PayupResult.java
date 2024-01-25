package com.anchor.global.nhpay.response;

public interface PayupResult {

  PayupResponseHeader getHeader();

  default boolean validateResponseCode() {
    return getHeader().getResponseCode()
        .equals("00000");
  }

  default String getMessage() {
    return getHeader().getResponseMessage();
  }
}
