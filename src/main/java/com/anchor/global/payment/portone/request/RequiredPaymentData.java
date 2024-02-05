package com.anchor.global.payment.portone.request;

import com.anchor.global.util.type.JsonSerializable;

public interface RequiredPaymentData extends JsonSerializable {

  String getImpUid();
}
