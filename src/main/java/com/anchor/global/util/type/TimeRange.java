package com.anchor.global.util.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimeRange {

  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
  private LocalTime from;

  @JsonSerialize(using = LocalTimeSerializer.class)
  @JsonDeserialize(using = LocalTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
  private LocalTime to;

  private TimeRange(LocalDateTime from, LocalDateTime to) {
    this.from = from.toLocalTime();
    this.to = to.toLocalTime();
  }

  public static TimeRange of(DateTimeRange dateTimeRange) {
    return new TimeRange(dateTimeRange.getFrom(), dateTimeRange.getTo());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TimeRange other) {
      return other.hashCode() == hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }
}
