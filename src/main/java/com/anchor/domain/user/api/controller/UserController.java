package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
public class UserController {

  private final UserService userService;
  private final HttpSession httpSession;

  @GetMapping("/me")
  public String getInfo(Model model){
  SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user"); //email, nickname, image
  String email = sessionUser.getEmail();
  UserInfoResponse userInfoResponse = userService.getProfile(email);
  model.addAttribute("user", userInfoResponse);
    return "내 프로필 페이지 조회";
  }

  @PutMapping("/me")
  public Map<String, Object> putInfo(@RequestBody UserNicknameRequest userNicknameRequest){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    String email = sessionUser.getEmail();
    userService.modifyNickname(email, userNicknameRequest);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("modify", "ok");
    return resultMap;
  }

  @PutMapping("/me/image")
  public Map<String, Object> putImage(@RequestBody UserNicknameRequest userNicknameRequest){
    return null;
  }

  @DeleteMapping("/me")
  public Map<String, Object> deleteUser(){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user"); //email, nickname, image
    String email = sessionUser.getEmail();
    userService.deleteUser(email);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("delete", "ok");
    return resultMap;
  }

}
