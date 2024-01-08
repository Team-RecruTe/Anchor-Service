package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.domain.mentor.api.service.response.MentorContents;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/mentors")
@Controller
public class MentorInfoViewController {

  private final MentorInfoService mentorInfoService;
  private final ViewResolver viewResolver;

  @GetMapping("/{id}")
  public String viewMentorPage(@PathVariable Long id, Model model){
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findInfo(id);
    model.addAttribute("mentorInfo",mentorInfoResponse);
    return viewResolver.getViewPath("mentor", "info");
  }

  @GetMapping("/me/introduction")
  public String viewMyIntroductionPage(HttpSession httpSession, Model model){
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findInfo(user.getMentorId());
    Long mentorIntroductionId = mentorInfoResponse.getMentorIntroduction() != null ? mentorInfoResponse.getMentorIntroduction().getId() : null;
    if (mentorIntroductionId != null) {
      MentorContents mentorContents = mentorInfoService.getContents(user.getMentorId(),mentorIntroductionId);
      model.addAttribute("mentorContents",mentorContents);
      return viewResolver.getViewPath("mentor", "contents-edit");
    }
    return viewResolver.getViewPath("common", "home");
  }

}