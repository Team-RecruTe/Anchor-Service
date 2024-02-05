package com.anchor.global.exception.type.payment;

import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.error.AnchorErrorCode;

public class PaymentResultNotFoundException extends ServiceException {

  public PaymentResultNotFoundException() {
    super(AnchorErrorCode.PAYMENT_NOT_FOUND);
  }
}
