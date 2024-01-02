package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeResponse;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringApplicationTime {

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;
  @JsonFormat(pattern = "HH:mm")
  private LocalTime time;

  @Builder
  private MentoringApplicationTime(LocalDate date, LocalTime time) {
    this.date = date;
    this.time = time;
  }

  public MentoringUnavailableTime convertToMentoringUnavailableTime(Mentor mentor) {
    LocalDateTime fromDateTime = getFromDateTime();
    LocalDateTime toDateTime = getToDateTime();

    return MentoringUnavailableTime.builder()
        .fromDateTime(fromDateTime)
        .toDateTime(toDateTime)
        .mentor(mentor)
        .build();
  }

  public MentoringUnavailableTimeResponse convertToMentoringUnavailableTimeResponse() {
    LocalDateTime fromDateTime = getFromDateTime();
    LocalDateTime toDateTime = getToDateTime();

    return MentoringUnavailableTimeResponse.builder()
        .fromDateTime(fromDateTime)
        .toDateTime(toDateTime)
        .build();
  }

  public LocalDateTime getFromDateTime() {
    return LocalDateTime.of(this.date, this.time);
  }

  public LocalDateTime getToDateTime() {
    return LocalDateTime.of(this.date, this.time)
        .plusHours(1L);
  }
}
