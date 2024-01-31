package com.anchor.global.util;

import com.anchor.global.util.type.Link;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class ResponseDto {

  public static String BASE_URL = "http://localhost:8080";
  protected List<Link> links;

  public void addLinks(List<Link> links) {
    this.links = links;
  }

}
