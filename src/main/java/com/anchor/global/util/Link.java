package com.anchor.global.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor
public class Link {

  @Value("${base.url}")
  private String baseUrl;
  private String rel;
  private String href;

  public Link(String rel, String path) {
    this.rel = rel;
    this.href = baseUrl + path;
  }

  public static LinkBuilder builder() {
    return new LinkBuilder();
  }

  public static class LinkBuilder {

    private final List<Link> links = new ArrayList<>();

    public LinkBuilder setLink(String rel, String path) {
      links.add(new Link(rel, path));
      return this;
    }

    public List<Link> build() {
      return links;
    }

  }

}
