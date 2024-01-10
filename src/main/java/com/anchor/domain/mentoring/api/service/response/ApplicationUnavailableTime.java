package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ApplicationUnavailableTime implements Serializable {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime fromDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime toDateTime;

  @Builder
  private ApplicationUnavailableTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
  }

  public ApplicationUnavailableTime(MentoringApplication application) {
    this.fromDateTime = application.getStartDateTime();
    this.toDateTime = application.getEndDateTime();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ApplicationUnavailableTime that)) {
      return false;
    }
    return Objects.equals(getFromDateTime(), that.getFromDateTime())
        && Objects.equals(getToDateTime(), that.getToDateTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFromDateTime(), getToDateTime());
  }
}
