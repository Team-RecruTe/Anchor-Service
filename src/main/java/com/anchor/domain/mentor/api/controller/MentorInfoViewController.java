package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.domain.mentor.api.service.response.MentorContents;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.BankCode;
import com.anchor.global.util.view.ViewResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@RequestMapping("/mentors")
@Controller
public class MentorInfoViewController {

  private final MentorInfoService mentorInfoService;
  private final ViewResolver viewResolver;

  /**
   * 멘토 페이지를 조회합니다.
   */
  @GetMapping("/{id}")
  public String viewMentorPage(@PathVariable Long id, Model model) {
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findInfo(id);
    model.addAttribute("mentorInfo", mentorInfoResponse);
    model.addAttribute("bankCodes", BankCode.values());
    model.addAttribute("careers", Career.values());
    return viewResolver.getViewPath("mentor", "info");
  }

  /**
   * 멘토 소개글 수정 페이지를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @GetMapping("/me/introduction")
  public String viewMentorIntroEditPage(@SessionAttribute("user") SessionUser user, Model model) {
    MentorContents contents = mentorInfoService.getContents(user.getMentorId());
    model.addAttribute("mentorContents", contents);
    return viewResolver.getViewPath("mentor", "contents-edit");
  }

}