package com.anchor.global.util;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Hateoas {

  private final List<Link> links = new ArrayList<>();

  @Getter
  @NoArgsConstructor
  private static class Link {

    private String rel;
    private String href;
    
  }

}
