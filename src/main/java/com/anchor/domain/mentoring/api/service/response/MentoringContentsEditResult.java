package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import lombok.Getter;

@Getter
public class MentoringContentsEditResult extends ResponseDto {

  private final Long id;

  public MentoringContentsEditResult(Long id) {
    this.id = id;
  }

}
