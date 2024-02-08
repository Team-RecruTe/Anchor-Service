package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.global.util.ResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContents extends ResponseDto {

  private String title;
  private String durationTime;
  private Integer cost;
  private String contents;
  private List<String> tags;

  public MentoringContents(String title, String durationTime, Integer cost, String contents, List<String> tags) {
    this.title = title;
    this.durationTime = durationTime;
    this.cost = cost;
    this.contents = contents;
    this.tags = tags;
  }

  public static MentoringContents of(Mentoring mentoring) {
    return new MentoringContents(
        mentoring.getTitle(),
        mentoring.getDurationTime(),
        mentoring.getCost(),
        mentoring.getContents(),
        mentoring.getTags()
    );
  }

}