package com.anchor.domain.mentoring.api.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContentsInfo {

  private String contents;

  public MentoringContentsInfo(String contents) {
    this.contents = contents;
  }

}