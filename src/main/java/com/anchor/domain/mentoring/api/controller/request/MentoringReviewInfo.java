package com.anchor.domain.mentoring.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentoringReviewInfo {

  @NotBlank(message = "상세 내용을 입력해주세요.")
  private String contents;

  @Builder
  public MentoringReviewInfo(String contents) {
    this.contents = contents;
  }

}
