package com.anchor.domain.mentor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Career {

  JUNIOR("1~3년차"),
  MIDDLE("4~6년차"),
  SENIOR("7~9년차"),
  LEADER("10년차 이상");

  private final String rangeOfYear;

}
