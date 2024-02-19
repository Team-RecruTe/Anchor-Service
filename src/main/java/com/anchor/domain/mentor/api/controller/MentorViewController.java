package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.BankCode;
import com.anchor.global.util.view.ViewResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequestMapping("/mentors")
@RequiredArgsConstructor
@Controller
public class MentorViewController {

  private final MentorService mentorService;
  private final ViewResolver viewResolver;

  /**
   * 멘토 등록 페이지를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("/register")
  public String viewMentorRegisterPage(Model model) {
    model.addAttribute("bankCodes", BankCode.values());
    model.addAttribute("careers", Career.values());
    return viewResolver.getViewPath("mentor", "register");
  }

  /**
   * 멘토 스케줄 페이지를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @GetMapping("/me/schedule")
  public String viewMentorSchedulePage(@SessionAttribute("user") SessionUser user, Model model) {
    MentorOpenCloseTimes result = mentorService.getMentorSchedule(user.getMentorId());
    model.addAttribute("openCloseTimes", result);
    return viewResolver.getViewPath("mentor", "schedule");
  }

  /**
   * 멘토링 신청내역 페이지를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @GetMapping("/me/applied-mentorings")
  public String viewMentoringApplicationListPage(
      @PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable,
      @SessionAttribute("user") SessionUser user, Model model) {
    Page<AppliedMentoringSearchResult> results = mentorService.getMentoringApplications(user.getMentorId(), pageable);
    model.addAttribute("mentoringApplications", results);
    return viewResolver.getViewPath("mentor", "mentoring-application");
  }

  /**
   * 멘토 정산 페이지를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @GetMapping("/me/payup")
  public String viewPayupPage() {
    return viewResolver.getViewPath("mentor", "payup");
  }

}
