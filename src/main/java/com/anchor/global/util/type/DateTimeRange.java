package com.anchor.global.util.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DateTimeRange implements Serializable {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime from;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime to;

  private DateTimeRange(LocalDateTime from, LocalDateTime to) {
    this.from = from;
    this.to = to;
  }

  public static DateTimeRange of(LocalDateTime from, LocalDateTime to) {
    return new DateTimeRange(from, to);
  }

  public String formattingFromTime() {
    return from.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
  }

  public boolean isDuration(LocalDateTime target) {
    return isOnOrBefore(target) && isOnOrAfter(target);
  }

  private boolean isOnOrBefore(LocalDateTime target) {
    return target.isBefore(to) || target.isEqual(to);
  }

  private boolean isOnOrAfter(LocalDateTime target) {
    return target.isAfter(from) || target.isEqual(from);
  }

  @Override
  public String toString() {
    return from.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        + " ~ " +
        to.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
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