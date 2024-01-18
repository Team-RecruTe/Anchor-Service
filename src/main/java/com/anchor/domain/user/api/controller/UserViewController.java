package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserViewController {

  private final UserService userService;
  private final ViewResolver viewResolver;

  @GetMapping("/me")
  public String getInfo(Model model, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    UserInfoResponse userInfoResponse = userService.getProfile(sessionUser.getEmail());
    model.addAttribute("userInfo", userInfoResponse);
    return viewResolver.getViewPath("user", "userInfo");
  }

  /**
   * 멘토링 신청내역을 조회합니다.
   */
  @GetMapping("/me/applied-mentorings")
  public String appliedMentoringList(@PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable,
      HttpSession session, Model model) {

    SessionUser sessionUser = SessionUser.getSessionUser(session);

    Page<AppliedMentoringInfo> appliedMentoringInfoList = userService.loadAppliedMentoringList(
        sessionUser, pageable);

    model.addAttribute("mentoringApplications", appliedMentoringInfoList);
    return viewResolver.getViewPath("user", "user-mentoring-application");
  }

}