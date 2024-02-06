package com.anchor.domain.mentor.domain;

import com.anchor.global.exception.type.entity.CareerNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Career {

  JUNIOR("1~3년차"),
  MIDDLE("4~6년차"),
  SENIOR("7~9년차"),
  LEADER("10년차 이상");

  private static final Map<String, Career> CAREER_MAP = Arrays.stream(Career.values())
      .collect(Collectors.toMap(Career::getRangeOfYear, Function.identity()));

  @JsonValue
  private final String rangeOfYear;

  @JsonCreator
  public static Career find(String rangeOfYear) {
    if (CAREER_MAP.containsKey(rangeOfYear)) {
      return CAREER_MAP.get(rangeOfYear);
    }
    throw new CareerNotFoundException();
  }

}
