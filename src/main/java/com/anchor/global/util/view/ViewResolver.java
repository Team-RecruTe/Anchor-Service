package com.anchor.global.util.view;

import org.springframework.stereotype.Component;

@Component
public class ViewResolver {

  private static final String DEFAULT_PAGE_PATH = "fragments/contents";

  public String getViewPath(String packageName, String viewName) {
    return DEFAULT_PAGE_PATH + "/" + packageName + "/" + viewName;
  }

}
