package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import lombok.Getter;

@Getter
public class MentoringCreateResult extends ResponseDto {

  private Long id;

  public MentoringCreateResult(Long id) {
    this.id = id;
  }

}
