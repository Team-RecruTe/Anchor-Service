package com.anchor.global.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Link {

  private String rel;
  private String href;

  public Link(String rel, String href) {
    this.rel = rel;
    this.href = href;
  }

  public static LinkBuilder builder() {
    return new LinkBuilder();
  }

  public static class LinkBuilder {

    private final List<Link> links = new ArrayList<>();

    public LinkBuilder setLink(String rel, String href) {
      links.add(new Link(rel, href));
      return this;
    }

    public List<Link> build() {
      return links;
    }

  }

}
