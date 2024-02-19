package com.anchor.domain.home.api.controller;

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

  /**
   * 인기 멘토링 10개, 인기 태그 10개와 포함해 홈페이지를 조회합니다.
   */
  @GetMapping({"", "/"})
  public String viewHomePage(Model model) {
    List<PopularTag> popularTags = mentoringService.getPopularTags();
    TopMentoring topMentorings = mentoringService.getTopMentorings();
    model.addAttribute("popularTags", popularTags);
    model.addAttribute("topMentorings", topMentorings);
    return viewResolver.getViewPath("common", "home");
  }

  /**
   * 로그인 페이지를 조회합니다.
   */
  @GetMapping("/login")
  public String viewLoginPage() {
    return viewResolver.getViewPath("common", "login");
  }
}
