package com.anchor.global.homepage.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularTagResponse {

  private List<String> tags;

  public PopularTagResponse(Mentoring mentoring) {
    this.tags = mentoring.getTags();
  }

}