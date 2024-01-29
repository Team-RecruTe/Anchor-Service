package com.anchor.global.nhpay.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NHRequestUrl {

  NH_ACCOUNT_HOLDER_URI("/InquireDepositorAccountNumber.nh"),
  NH_DEPOSIT_URI("/ReceivedTransferAccountNumber.nh"),
  OTHER_ACCOUNT_HOLDER_URI("/InquireDepositorOtherBank.nh"),
  OTHER_DEPOSIT_URI("/ReceivedTransferOtherBank.nh");

  private final String url;
}
