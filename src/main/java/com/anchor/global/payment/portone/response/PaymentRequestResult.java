package com.anchor.global.payment.portone.response;

import com.anchor.global.exception.type.payment.PaymentValidationException;
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
    throw new PaymentValidationException(message);
  }

  private boolean isSuccess() {
    return code.equals(SUCCESS_CODE);
  }

}
