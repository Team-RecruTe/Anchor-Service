package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringTag;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentoringDetailInfo {

  private String title;
  private String durationTime;
  private String content;
  private String nickname;
  private Integer cost;
  private List<String> tags;

  @Builder
  private MentoringDetailInfo(String title, String durationTime, String content,
      String nickname,
      Integer cost, List<String> tags) {
    this.title = title;
    this.durationTime = durationTime;
    this.content = content;
    this.nickname = nickname;
    this.cost = cost;
    this.tags = tags;
  }

  public MentoringDetailInfo(Mentoring mentoring) {

    this.title = mentoring.getTitle();

    this.durationTime = mentoring.getDurationTime();

    this.content = mentoring.getMentoringDetail()
        .getContents();

    this.nickname = mentoring.getMentor()
        .getUser()
        .getNickname();
    this.cost = mentoring.getCost();

    this.tags = mentoring.getMentoringTag()
        .stream()
        .map(MentoringTag::getTag)
        .toList();
  }
}
