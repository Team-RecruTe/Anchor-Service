package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mentors")
@RequiredArgsConstructor
@Controller
public class MentorViewController {

  private final MentorService mentorService;
  private final ViewResolver viewResolver;

  @GetMapping("/me/schedule")
  public String getUnavailableTimes(HttpSession httpSession, Model model) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentorOpenCloseTimes result = mentorService.getMentorSchedule(1L);
    model.addAttribute("openCloseTimes", result);
    return viewResolver.getViewPath("mentor", "schedule");
  }

  @GetMapping("/me/applied-mentorings")
  public String getMentoringApplications(
      @PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable, HttpSession httpSession,
      Model model) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    Page<AppliedMentoringSearchResult> results = mentorService.getMentoringApplications(1L, pageable);
    model.addAttribute("mentoringApplications", results);
    return viewResolver.getViewPath("mentor", "mentoring-application");
  }

  @GetMapping("/register")
  public String register() {
    return "/register";
  }

  @PostMapping("")
  public String registerProcess(@ModelAttribute MentorRegisterInfo mentorRegisterInfo) {
    mentorService.register(mentorRegisterInfo);
    return "멘토 등록 완료. 로그인 페이지로 이동해주세요.";
  }

  @GetMapping("/me/payup")
  public String viewPayupPage() {
    return viewResolver.getViewPath("mentor", "payup");
  }

}
