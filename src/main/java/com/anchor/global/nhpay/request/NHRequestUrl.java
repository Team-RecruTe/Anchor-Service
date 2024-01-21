package com.anchor.global.nhpay.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NHRequestUrl {

  ACCOUNT_HOLDER_URI("/InquireDepositorOtherBank.nh"),
  DEPOSIT_URI("/ReceivedTransferOtherBank.nh");

  private final String url;
}
