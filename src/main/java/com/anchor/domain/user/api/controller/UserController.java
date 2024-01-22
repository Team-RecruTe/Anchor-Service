package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.UserImageRequest;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping
@RestController("/users")
public class UserController {

  private static final String SUCCESS = "success";
  private static final String FAILURE = "failure";
  private final UserService userService;


  /**
   *  유저 닉네임 변경
   */
  @PutMapping("/me")
  public ResponseEntity<String> putInfo(@RequestBody UserNicknameRequest userNicknameRequest, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    userService.editNickname(sessionUser.getEmail(), userNicknameRequest);
    return ResponseEntity.ok().build();
  }

  /**
   *  유저 프로필 이미지 변경
   */
  @PutMapping("/me/image")
  public ResponseEntity<String> putImage(@RequestBody UserImageRequest userImageRequest, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    userService.uploadImage(sessionUser.getEmail(), userImageRequest);
    return ResponseEntity.ok().build();
  }

  /**
   *  유저 탈퇴
   */
  @DeleteMapping("/me")
  public ResponseEntity<String> deleteUser(HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    userService.deleteUser(sessionUser.getEmail());
    return ResponseEntity.ok().build();
  }

  /**
   * 신청한 멘토링의 상태를 변경합니다. 취소, 또는 완료로 변경가능합니다.
   */
  @PutMapping("/me/applied-mentorings")
  public ResponseEntity<String> appliedMentoringStatusChange(@RequestBody MentoringStatusInfo mentoringStatus,
      HttpSession session) {

    SessionUser sessionUser = SessionUser.getSessionUser(session);

    boolean changeStatusResult = userService.changeAppliedMentoringStatus(sessionUser, mentoringStatus);

    if (changeStatusResult) {
      return ResponseEntity.ok()
          .body(SUCCESS);
    } else {
      return ResponseEntity.badRequest()
          .build();
    }
  }

}
