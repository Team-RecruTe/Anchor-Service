package com.anchor.global.portone.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestResult {

  private static final Integer SUCCESS_CODE = 0;
  private Integer code;
  private String message;
  private Object response;

  protected PaymentRequestResult(Integer code, String message, Object response) {
    this.code = code;
    this.message = message;
    this.response = response;
  }

  public Object getResponse() {
    if (isSuccess()) {
      return response;
    }
    throw new RuntimeException(message);
  }

  private boolean isSuccess() {
    return code.equals(SUCCESS_CODE);
  }

}
