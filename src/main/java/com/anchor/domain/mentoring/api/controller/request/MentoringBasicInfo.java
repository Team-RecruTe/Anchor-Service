package com.anchor.domain.mentoring.api.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringBasicInfo {

  @NotBlank(message = "제목을 입력해주세요.")
  @Max(50)
  private String title;

  @NotBlank
  @Pattern(regexp = "^(?:(1[0-2]|[1-9])h)?(?:10m|20m|30m|40m|50m)$", message = "시간(h)이 분(m)보다 앞에 와야 하며, 1시간 단위 혹은 10분 단위로 등록할 수 있습니다.")
  private String durationTime;

  @NotNull(message = "비용을 입력해주세요.")
  private Integer cost;

  @Builder
  private MentoringBasicInfo(String title, String durationTime, Integer cost) {
    this.title = title;
    this.durationTime = durationTime;
    this.cost = cost;
  }

}
