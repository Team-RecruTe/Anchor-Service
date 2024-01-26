package com.anchor.domain.mentoring.api.service.response;

import java.beans.ConstructorProperties;
import java.util.Comparator;
import lombok.Getter;

@Getter
public class PopularTag implements Comparable<PopularTag> {

  private String tagName;
  private Long tagCount;

  @ConstructorProperties({"tagName", "tagCount"})
  public PopularTag(String tagName, Long tagCount) {
    this.tagName = tagName;
    this.tagCount = tagCount;
  }

  @Override
  public int compareTo(PopularTag other) {
    return Comparator.comparing(PopularTag::getTagName)
        .compare(this, other);
  }
}
