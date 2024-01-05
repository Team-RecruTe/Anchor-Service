package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users/me")
@RestController
public class UserController {

  private final UserService userService;

  @PutMapping
  public ResponseEntity<String> putInfo(@RequestBody UserNicknameRequest userNicknameRequest, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    userService.editNickname(sessionUser.getEmail(), userNicknameRequest);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/image")
  public ResponseEntity<String> putImage(@RequestBody UserNicknameRequest userNicknameRequest, HttpSession httpSession){
    return ResponseEntity.ok().build();
  }

  @DeleteMapping
  public ResponseEntity<String> deleteUser(HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user"); //email, nickname, image
    userService.deleteUser(sessionUser.getEmail());
    return ResponseEntity.ok().build();
  }

}
