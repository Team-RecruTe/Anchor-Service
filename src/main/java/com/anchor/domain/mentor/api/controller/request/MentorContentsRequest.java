package com.anchor.domain.mentor.api.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorContentsRequest {

  private String contents;

  public MentorContentsRequest(String contents) {
    this.contents = contents;
  }

}