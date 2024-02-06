package com.anchor.domain.mentoring.domain;

import com.anchor.global.exception.type.entity.MentoringStatusNotFoundException;
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
public enum MentoringStatus {

  WAITING("대기"),
  CANCELLED("취소"),
  APPROVAL("승인"),
  COMPLETE("완료");

  private static final Map<String, MentoringStatus> MENTORING_STATUS_MAP = Arrays.stream(MentoringStatus.values())
      .collect(Collectors.toMap(MentoringStatus::getDescription, Function.identity()));

  @JsonValue
  private final String description;

  @JsonCreator
  public static MentoringStatus find(String name) {
    if (MENTORING_STATUS_MAP.containsKey(name)) {
      return MENTORING_STATUS_MAP.get(name);
    }
    throw new MentoringStatusNotFoundException();
  }

  public boolean isEqualTo(String status) {
    return status.toUpperCase()
        .equals(this.name());
  }
}