package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import lombok.Getter;

@Getter
public class MentoringDefaultInfo {

  private String title;
  private String nickname;
  private String durationTime;
  private Integer cost;

  public MentoringDefaultInfo(Mentoring mentoring) {
    this.title = mentoring.getTitle();
    this.nickname = mentoring.getMentor()
        .getUser()
        .getNickname();
    this.durationTime = mentoring.getDurationTime();
    this.cost = mentoring.getCost();
  }
}
