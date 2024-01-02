package com.anchor.domain.user.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppliedMentoringStatus {

  @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
  private LocalDateTime startDateTime;
  @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
  private LocalDateTime endDateTime;

  private MentoringStatus status;


  @Builder
  private AppliedMentoringStatus(LocalDateTime startDateTime, LocalDateTime endDateTime,
      MentoringStatus status) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.status = status;
  }
}
