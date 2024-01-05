package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserViewController {

  private final UserService userService;

  @GetMapping("/me")
  public String getInfo(Model model, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user"); //email, nickname, image
    String email = sessionUser.getEmail();
    UserInfoResponse userInfoResponse = userService.getProfile(email);
    model.addAttribute("user", userInfoResponse);
    return "내 프로필 페이지 조회";
  }

}
