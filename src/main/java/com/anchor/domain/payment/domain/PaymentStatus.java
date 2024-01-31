package com.anchor.domain.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

  SUCCESS("성공"),
  CANCELLED("취소");

  private final String description;

}
