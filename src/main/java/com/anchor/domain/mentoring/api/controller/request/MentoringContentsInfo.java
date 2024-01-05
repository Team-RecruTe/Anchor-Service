package com.anchor.domain.mentoring.api.controller.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringContentsInfo {

  @NotBlank(message = "상세 내용을 입력해주세요.")
  private String contents;

  private List<String> tags;

  public MentoringContentsInfo(String contents, List<String> tags) {
    this.tags = tags;
    this.contents = contents;
  }

}