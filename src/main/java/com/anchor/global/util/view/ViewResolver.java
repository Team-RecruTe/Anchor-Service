package com.anchor.global.util.view;

import org.springframework.stereotype.Component;

@Component
public class ViewResolver {

  private static final String MAIN_VIEW_PATH = "main/";

  public String getViewName(String viewName) {
    return MAIN_VIEW_PATH + viewName;
  }

}
