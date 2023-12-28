package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringStatusUtils;
import com.anchor.global.util.DateTimeRange;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringStatusInfos {

  List<MentoringStatusInfo> mentoringStatusInfos;

  @Getter
  @NoArgsConstructor
  public static class MentoringStatusInfo {

    private DateTimeRange mentoringEstimatedTime;
    private String mentoringStatus;

    public MentoringStatus getMentoringStatus() {
      return MentoringStatusUtils.of(mentoringStatus);
    }
  }

}
