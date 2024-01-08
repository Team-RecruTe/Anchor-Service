package com.anchor.domain.mentoring.api.service.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContentsResult {

  private String contents;

  public MentoringContentsResult(String contents) {
    this.contents = contents;
  }

}