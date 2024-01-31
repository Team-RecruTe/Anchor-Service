package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContents extends ResponseDto {

  private String title;
  private String contents;
  private List<String> tags;

  public MentoringContents(String title, String contents, List<String> tags) {
    this.title = title;
    this.contents = contents;
    this.tags = tags;
  }

}