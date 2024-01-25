package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.domain.mentor.api.service.response.MentorContents;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.BankCode;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mentors")
@Controller
public class MentorInfoViewController {

  private final MentorInfoService mentorInfoService;
  private final ViewResolver viewResolver;

  @GetMapping("/{id}")
  public String viewMentorPage(@PathVariable Long id, Model model) {
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findInfo(id);
    model.addAttribute("mentorInfo", mentorInfoResponse);
    model.addAttribute("bankCodes", BankCode.values());
    model.addAttribute("careers", Career.values());
    return viewResolver.getViewPath("mentor", "info");
  }

  @GetMapping("/me/introduction")
  public String editIntroductionPage(HttpSession session, Model model) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    MentorContents contents = mentorInfoService.getContents(sessionUser);
    model.addAttribute("mentorContents", contents);
    return viewResolver.getViewPath("mentor", "contents-edit");
  }

}