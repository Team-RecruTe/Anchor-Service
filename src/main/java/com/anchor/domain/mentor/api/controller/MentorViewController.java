package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mentors/me")
@RequiredArgsConstructor
@Controller
public class MentorViewController {

  private final MentorService mentorService;

  @GetMapping("/applied-mentorings")
  public String getMentoringApplications(
      @PageableDefault(direction = Sort.Direction.DESC) Pageable pageable, HttpSession httpSession, Model model) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    Page<AppliedMentoringSearchResult> results = mentorService.getMentoringApplications(
        user.getMentorId(), pageable);
    model.addAttribute("mentoringApplications", results);
    return "mentoring-dashboard";
  }


}
