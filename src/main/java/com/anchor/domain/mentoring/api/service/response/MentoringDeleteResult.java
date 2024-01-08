package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;

public class MentoringDeleteResult extends ResponseDto {

  private final Long id;

  public MentoringDeleteResult(Long id) {
    this.id = id;
  }

}
