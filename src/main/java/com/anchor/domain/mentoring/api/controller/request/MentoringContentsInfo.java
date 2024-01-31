package com.anchor.domain.mentoring.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContentsInfo {

  @NotBlank(message = "상세 내용을 입력해주세요.")
  private String contents;

  private List<String> tags;

  private List<Long> imageIds;

  @Builder
  public MentoringContentsInfo(String contents, List<String> tags, List<Long> imageIds) {
    this.tags = tags;
    this.contents = contents;
    this.imageIds = imageIds;
  }

}