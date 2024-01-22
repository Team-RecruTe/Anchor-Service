package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.ResponseDto;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TopMentoring extends ResponseDto {

  private List<MentoringSearchResult> mentoringRank;

  public TopMentoring(List<MentoringSearchResult> mentoringRank) {
    this.mentoringRank = mentoringRank;
  }

}
