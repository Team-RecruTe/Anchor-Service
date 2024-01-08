package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContents extends ResponseDto {

  private String contents;
  private List<String> tags;

  public MentoringContents(String contents, List<String> tags) {
    this.contents = contents;
    this.tags = tags;
  }

}