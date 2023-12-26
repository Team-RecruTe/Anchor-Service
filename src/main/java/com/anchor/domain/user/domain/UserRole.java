package com.anchor.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

  ADMIN("ROLE_ADMIN", "관리자"),
  USER("ROLE_USER", "회원"),
  MENTOR("ROLE_MENTOR", "멘토");

  public final String key;
  public final String description;

}
