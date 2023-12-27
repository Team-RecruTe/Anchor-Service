package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeDto;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record MentoringApplicationTimeDto(
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
    @JsonFormat(pattern = "HH:mm") LocalTime time
) {

  public MentoringUnavailableTime convertToMentoringUnavailableTime(Mentor mentor) {
    LocalDateTime fromDateTime = LocalDateTime.of(this.date, this.time);
    LocalDateTime toDateTime = LocalDateTime.of(this.date, this.time)
        .plusHours(1L);

    return MentoringUnavailableTime.builder()
        .fromDateTime(fromDateTime)
        .toDateTime(toDateTime)
        .mentor(mentor)
        .build();
  }

  public MentoringUnavailableTimeDto convertToMentoringUnavailableTimeDto() {
    LocalDateTime fromDateTime = LocalDateTime.of(this.date, this.time);
    LocalDateTime toDateTime = LocalDateTime.of(this.date, this.time)
        .plusHours(1L);

    return new MentoringUnavailableTimeDto(fromDateTime, toDateTime);
  }
}
