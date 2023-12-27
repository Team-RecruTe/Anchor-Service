package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import lombok.Getter;

@Getter
public class MentoringEditResult {

  private String title;
  private String durationTime;
  private Integer cost;

  private MentoringEditResult(String title, String durationTime, Integer cost) {
    this.title = title;
    this.durationTime = durationTime;
    this.cost = cost;
  }

  public static MentoringEditResult of(Mentoring editedMentoring) {
    return new MentoringEditResult(editedMentoring.getTitle(), editedMentoring.getDurationTime(),
        editedMentoring.getCost());
  }

}
