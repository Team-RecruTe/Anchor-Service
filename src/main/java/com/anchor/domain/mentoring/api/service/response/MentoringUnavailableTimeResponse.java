package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentoringUnavailableTimeResponse {

  private LocalDateTime fromDateTime;
  private LocalDateTime toDateTime;

  @Builder
  private MentoringUnavailableTimeResponse(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
  }

  public MentoringUnavailableTimeResponse(MentoringUnavailableTime unavailableTime) {
    this.fromDateTime = unavailableTime.getFromDateTime();
    this.toDateTime = unavailableTime.getToDateTime();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MentoringUnavailableTimeResponse that)) {
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
