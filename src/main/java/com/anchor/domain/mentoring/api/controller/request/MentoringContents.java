package com.anchor.domain.mentoring.api.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContents {

  private String contents;

  public MentoringContents(String contents) {
    this.contents = contents;
  }

}