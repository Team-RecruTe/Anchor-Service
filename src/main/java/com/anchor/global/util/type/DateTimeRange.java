package com.anchor.global.util.type;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
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