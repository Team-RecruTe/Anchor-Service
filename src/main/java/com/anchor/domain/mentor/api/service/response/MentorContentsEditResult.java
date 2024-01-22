package com.anchor.domain.mentor.api.service.response;

import com.anchor.global.util.ResponseDto;
import lombok.Getter;

@Getter
public class MentorContentsEditResult extends ResponseDto {

  private Long id;

  public MentorContentsEditResult(Long id) {
    this.id = id;
  }
}
