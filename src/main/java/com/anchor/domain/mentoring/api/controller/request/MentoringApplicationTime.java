package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.global.exception.type.mentoring.DurationTimeParsingException;
import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;

@Getter
public class MentoringApplicationTime {

  private static final Pattern pattern = Pattern.compile("(\\d+)h(?:([1-5]0)m)?");

  @JsonProperty("date")
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;
  @JsonProperty("time")
  @JsonFormat(pattern = "HH:mm")
  private LocalTime time;
  private String durationTime;

  @ConstructorProperties({"date", "time", "durationTime"})
  private MentoringApplicationTime(LocalDate date, LocalTime time, String durationTime) {
    this.date = date;
    this.time = time;
    this.durationTime = durationTime;
  }

  public static MentoringApplicationTime of(LocalDate date, LocalTime time, String durationTime) {
    return new MentoringApplicationTime(date, time, durationTime);
  }

  public DateTimeRange convertDateTimeRange() {
    return DateTimeRange.of(getFromDateTime(), getToDateTime());
  }

  public LocalDateTime getFromDateTime() {
    return LocalDateTime.of(this.date, this.time);
  }

  public LocalDateTime getToDateTime() {
    Matcher matcher = pattern.matcher(durationTime);
    if (matcher.find()) {
      long hour = Long.parseLong(matcher.group(1));
      long minute = matcher.group(2) == null ? 0L : Long.parseLong(matcher.group(2));
      return LocalDateTime.of(this.date, this.time)
          .plusHours(hour)
          .plusMinutes(minute);
    }
    throw new DurationTimeParsingException();
  }
}
