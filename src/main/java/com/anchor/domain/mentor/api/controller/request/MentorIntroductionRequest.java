package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentor.domain.MentorIntroduction;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorIntroductionRequest {

  private String contents;

  public MentorIntroductionRequest(String contents) {
    this.contents = contents;
  }

}