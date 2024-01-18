package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 멘토링 신청페이지 데이터입니다.
 */
@Getter
@NoArgsConstructor
public class MentoringDefaultInfo {

  private Long mentoringId;
  private String title;
  private String durationTime;

  private MentoringDefaultInfo(Long mentoringId, String title, String durationTime) {
    this.mentoringId = mentoringId;
    this.title = title;
    this.durationTime = durationTime;
  }

  public static MentoringDefaultInfo of(Mentoring mentoring) {
    return new MentoringDefaultInfo(mentoring.getId(), mentoring.getTitle(), mentoring.getDurationTime());
  }
}
