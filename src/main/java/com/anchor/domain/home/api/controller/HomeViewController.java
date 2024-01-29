package com.anchor.global.homepage.api.controller;

import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.homepage.api.service.HomeService;
import com.anchor.global.homepage.api.service.response.PopularTagResponse;
import com.anchor.global.util.view.ViewResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class HomeViewController {

  private final ViewResolver viewResolver;
  private final HomeService homeService;

  @GetMapping({"", "/"})
  public String viewHome(Model model) {
    PopularTagResponse popularTags = homeService.getPopularTags();
    TopMentoring topMentorings = homeService.getTopMentorings();
    model.addAttribute("popularTags", popularTags);
    model.addAttribute("topMentorings", topMentorings);
    return viewResolver.getViewPath("common", "home");
  }

  @GetMapping("/login")
  public String viewLogin() {
    return viewResolver.getViewPath("common", "login");
  }
}
