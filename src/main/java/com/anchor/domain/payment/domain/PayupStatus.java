package com.anchor.domain.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayupStatus {

  WAITING("정산 대기"),
  COMPLETE("정산 완료");

  private final String description;

}
