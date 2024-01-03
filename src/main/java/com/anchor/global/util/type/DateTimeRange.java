package com.anchor.global.util.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DateTimeRange {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime from;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime to;

  private DateTimeRange(LocalDateTime from, LocalDateTime to) {
    this.from = from;
    this.to = to;
  }

  public static DateTimeRange of(LocalDateTime from, LocalDateTime to) {
    return new DateTimeRange(from, to);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof DateTimeRange other) {
      return other.hashCode() == hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
  }

}