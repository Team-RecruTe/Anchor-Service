package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import java.time.LocalDateTime;
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
}
