package com.anchor.global.homepage.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.PopularTag;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.util.view.ViewResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class HomeViewController {

  private final ViewResolver viewResolver;
  private final MentoringService mentoringService;

  @GetMapping({"", "/"})
  public String viewHome(Model model) {
    List<PopularTag> popularTags = mentoringService.getPopularTags();
    TopMentoring topMentorings = mentoringService.getTopMentorings();
    model.addAttribute("popularTags", popularTags);
    model.addAttribute("topMentorings", topMentorings);
    return viewResolver.getViewPath("common", "home");
  }

  @GetMapping("/login")
  public String viewLogin() {
    return viewResolver.getViewPath("common", "login");
  }
}
