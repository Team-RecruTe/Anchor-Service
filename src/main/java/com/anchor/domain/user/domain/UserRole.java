package com.anchor.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

  ADMIN("관리자"),
  USER("회원"),
  MENTOR("멘티");

  private final String description;

}
