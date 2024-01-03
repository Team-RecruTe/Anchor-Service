package com.anchor.global.util.type;

import com.anchor.global.util.ResponseDto;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Link {

  private String rel;
  private String href;

  public Link(String rel, String path) {
    this.rel = rel;
    this.href = ResponseDto.BASE_URL + path;
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
