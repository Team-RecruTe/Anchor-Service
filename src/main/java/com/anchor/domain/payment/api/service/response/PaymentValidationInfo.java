package com.anchor.domain.payment.api.service.response;


import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.global.util.ResponseType;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class PaymentValidationInfo implements Serializable {

  String validationResult;
  String impUid;
  String merchantUid;
  Integer amount;

  public PaymentValidationInfo(PaymentResultInfo paymentResultInfo, ResponseType responseType) {
    this.validationResult = responseType.name();
    this.impUid = paymentResultInfo.getImpUid();
    this.merchantUid = paymentResultInfo.getMerchantUid();
    this.amount = paymentResultInfo.getAmount();
  }
}
