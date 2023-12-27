package com.anchor.domain.mentoring.api.service.response;

import java.time.LocalDateTime;

public record MentoringUnavailableTimeDto(
    LocalDateTime fromDateTime,
    LocalDateTime toDateTime
) {

  public MentoringUnavailableTimeDto(
      com.anchor.domain.mentoring.domain.MentoringUnavailableTime unavailableTime) {
    this(
        unavailableTime.getFromDateTime(),
        unavailableTime.getToDateTime()
    );
  }
}
