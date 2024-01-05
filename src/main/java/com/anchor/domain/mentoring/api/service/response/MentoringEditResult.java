package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import lombok.Getter;

@Getter
public class MentoringEditResult extends ResponseDto {

  private Long id;

  public MentoringEditResult(Long id) {
    this.id = id;
  }

}
