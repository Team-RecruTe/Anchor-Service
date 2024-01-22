package com.anchor.domain.user.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringStatusInfo {

  List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos;

  @Builder
  private MentoringStatusInfo(List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    this.requiredMentoringStatusInfos = requiredMentoringStatusInfos;
  }

  @Getter
  @NoArgsConstructor
  public static class RequiredMentoringStatusInfo {

    private DateTimeRange mentoringReservedTime;

    private MentoringStatus mentoringStatus;

    @Builder
    private RequiredMentoringStatusInfo(LocalDateTime startDateTime, LocalDateTime endDateTime,
        MentoringStatus mentoringStatus) {
      this.mentoringReservedTime = DateTimeRange.of(startDateTime, endDateTime);
      this.mentoringStatus = mentoringStatus;
    }

  }

}
