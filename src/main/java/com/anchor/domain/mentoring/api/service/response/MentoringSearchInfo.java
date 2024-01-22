package com.anchor.domain.mentoring.api.service.response;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
public class MentoringSearchInfo {

  private Page<MentoringSearchResult> mentoringInfos;

  private Set<String> tags;

  private MentoringSearchInfo(Page<MentoringSearchResult> mentoringInfos, Set<String> tags) {
    this.mentoringInfos = mentoringInfos;
    this.tags = tags;
  }

  public static MentoringSearchInfo of(Page<MentoringSearchResult> mentoringInfos, Set<String> tags) {
    return new MentoringSearchInfo(mentoringInfos, tags);
  }
}
