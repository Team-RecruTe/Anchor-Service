package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringStatusUtils;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringStatusInfo {

  List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos;

  @Getter
  @NoArgsConstructor
  public static class RequiredMentoringStatusInfo {

    private DateTimeRange mentoringReservedTime;
    private String mentoringStatus;

    @Builder
    private RequiredMentoringStatusInfo(DateTimeRange mentoringReservedTime, String mentoringStatus) {
      this.mentoringReservedTime = mentoringReservedTime;
      this.mentoringStatus = mentoringStatus;
    }

    public MentoringStatus getMentoringStatus() {
      return MentoringStatusUtils.of(mentoringStatus);
    }
  }

}
