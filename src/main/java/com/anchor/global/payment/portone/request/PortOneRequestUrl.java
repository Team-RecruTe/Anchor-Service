package com.anchor.global.payment.portone.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortOneRequestUrl {

  ACCESS_TOKEN_URL("/users/getToken"),
  CANCEL_PAYMENT_URL("/payments/cancel"),
  CREATE_PAYMENT_URL("/payments/");

  private final String url;
}
