package com.anchor.domain.mentoring.api.service.response;

import lombok.Getter;

@Getter
public class MentoringCreationResult {

  private Long mentoringId;

  public MentoringCreationResult(Long mentoringId) {
    this.mentoringId = mentoringId;
  }

}
