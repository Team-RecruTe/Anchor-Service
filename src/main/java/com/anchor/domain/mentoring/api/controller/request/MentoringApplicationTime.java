package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.response.ApplicationUnavailableTime;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  @JsonProperty("duration_time")
  private String durationTime;

  @Builder
  private MentoringApplicationTime(LocalDate date, LocalTime time, String durationTime) {
    this.date = date;
    this.time = time;
    this.durationTime = durationTime;
  }

  public MentoringUnavailableTime convertToMentoringUnavailableTime(Mentor mentor) {
    LocalDateTime fromDateTime = getFromDateTime();
    LocalDateTime toDateTime = getToDateTime();

    return new MentoringUnavailableTime(fromDateTime, toDateTime, mentor);
  }

  public ApplicationUnavailableTime convertToMentoringUnavailableTimeResponse() {
    LocalDateTime fromDateTime = getFromDateTime();
    LocalDateTime toDateTime = getToDateTime();

    return ApplicationUnavailableTime.builder()
        .fromDateTime(fromDateTime)
        .toDateTime(toDateTime)
        .build();
  }

  public LocalDateTime getFromDateTime() {
    return LocalDateTime.of(this.date, this.time);
  }

  public LocalDateTime getToDateTime() {
    Matcher matcher = Pattern.compile("(\\d+)h(?:([1-5]0)m)?")
        .matcher(durationTime);

    if (matcher.find()) {

      long hour = Long.parseLong(matcher.group(1));
      long minute = matcher.group(2) == null ? 0L : Long.parseLong(matcher.group(2));

      return LocalDateTime.of(this.date, this.time)
          .plusHours(hour)
          .plusMinutes(minute);
    }
    throw new RuntimeException("올바르지 않은 durationTime 형식입니다.");
  }
}
