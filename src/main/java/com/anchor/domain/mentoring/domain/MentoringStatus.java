package com.anchor.domain.mentoring.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MentoringStatus {

  WAITING("대기"),
  CANCELLED("취소"),
  APPROVAL("승인"),
  COMPLETE("완료");

  @JsonValue
  private final String description;

  @JsonCreator
  public static MentoringStatus fromDescription(String description) {
    for (MentoringStatus status : MentoringStatus.values()) {
      if (status.getDescription()
          .equals(description)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid description :: " + description);
  }

  public boolean isEqualTo(String status) {
    return status.toUpperCase()
        .equals(this.name());
  }

}
