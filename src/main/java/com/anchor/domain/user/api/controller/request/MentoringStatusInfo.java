package com.anchor.domain.user.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringStatusInfo {

  List<RequiredMentoringStatusInfo> mentoringStatusList;

  @Builder
  private MentoringStatusInfo(List<RequiredMentoringStatusInfo> mentoringStatusList) {
    this.mentoringStatusList = mentoringStatusList;
  }

  @Getter
  @NoArgsConstructor
  public static class RequiredMentoringStatusInfo {

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime startDateTime;
    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    private MentoringStatus status;


    @Builder
    private RequiredMentoringStatusInfo(LocalDateTime startDateTime, LocalDateTime endDateTime,
        MentoringStatus status) {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.status = status;
    }

    public boolean mentoringStatusIsCanceledOrComplete() {

      if (status.equals(MentoringStatus.CANCELED) || status.equals(MentoringStatus.COMPLETE)) {
        return true;
      } else {
        throw new IllegalArgumentException("변경하려는 상태가 CANCELED 또는 COMPLETE 가 아닙니다.");
      }

    }
  }
}
