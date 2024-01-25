package com.anchor.global.homepage.api.service.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularTagResponse {

  private List<String> tags;

  public PopularTagResponse(List<String> tags) {
    this.tags = tags;
  }

}