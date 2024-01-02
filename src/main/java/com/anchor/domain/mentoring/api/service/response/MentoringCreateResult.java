package com.anchor.domain.mentoring.api.service.response;

import com.anchor.global.util.Link;
import java.util.List;
import lombok.Getter;

@Getter
public class MentoringCreateResult {

  private Long id;
  private List<Link> links;

  public MentoringCreateResult(Long id) {
    this.id = id;
  }

  public void addLinks(List<Link> links) {
    this.links = links;
  }

}
