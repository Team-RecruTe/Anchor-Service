package com.anchor.global.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SessionKeyType {

  ECODE("ecode", "이메일 인증코드"),
  USER("user", "로그인 인증Key"),
  MERCHANT_UID("merchantUid", "결제고유번호");

  private final String key;

  private final String description;
}
