package com.anchor.domain.payment.api.service.response;


import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.global.util.ResponseType;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class PaymentValidationInfo implements Serializable {

  String validationResult;
  @JsonProperty("imp_uid")
  String impUid;
  @JsonProperty("merchant_uid")
  String merchantUid;
  Integer amount;

  public PaymentValidationInfo(PaymentResultInfo paymentResultInfo, ResponseType responseType) {
    this.validationResult = responseType.name();
    this.impUid = paymentResultInfo.getImpUid();
    this.merchantUid = paymentResultInfo.getMerchantUid();
    this.amount = paymentResultInfo.getAmount();
  }
}
