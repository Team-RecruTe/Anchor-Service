package com.anchor.global.homepage.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.homepage.api.service.HomeService;
import com.anchor.global.homepage.api.service.response.PopularTagResponse;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@Controller
public class HomeViewController {

  private final ViewResolver viewResolver;
  private final HomeService homeService;

  @GetMapping({"","/"})
  public String viewHome(Model model, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    List<PopularTagResponse> popularTags = homeService.getPopularTags();
    model.addAttribute("popularTags", popularTags);
    TopMentoring topMentorings = homeService.getTopMentorings();
    model.addAttribute("topMentorings", topMentorings);
    return viewResolver.getViewPath("common", "home");
  }

}
