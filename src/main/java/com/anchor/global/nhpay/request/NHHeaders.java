package com.anchor.global.nhpay.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NHHeaders {
  ACCOUNT_HOLDER_API_NAME("InquireDepositorOtherBank"),
  ACCOUNT_HOLDER_SEARCH_API_CODE("DrawingTransferA"),
  DEPOSIT_API_NAME("ReceivedTransferOtherBank"),
  DEPOSIT_API_CODE("ReceivedTransferA"),
  FINTECH_APS_NO("001");

  private final String value;
}
