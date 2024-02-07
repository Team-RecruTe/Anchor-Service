package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.UserImageRequest;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.ResponseType;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  /**
   * 유저 닉네임 변경
   */
  @PutMapping("/me")
  public ResponseEntity<ResponseType> putInfo(@RequestBody UserNicknameRequest userNicknameRequest,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    userService.editNickname(sessionUser, userNicknameRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 유저 프로필 이미지 변경
   */
  @PutMapping("/me/image")
  public ResponseEntity<ResponseType> putImage(@RequestBody UserImageRequest userImageRequest, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    userService.uploadImage(sessionUser, userImageRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 유저 탈퇴
   */
  @DeleteMapping("/me")
  public ResponseEntity<ResponseType> deleteUser(HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    userService.deleteUser(sessionUser);
    session.invalidate();
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }


  /**
   * 신청한 멘토링의 상태를 변경합니다. 취소, 또는 완료로 변경가능합니다.
   */
  @PutMapping("/me/applied-mentorings")
  public ResponseEntity<ResponseType> appliedMentoringStatusChange(@RequestBody MentoringStatusInfo mentoringStatus,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    boolean changeStatusResult = userService.changeMentoringStatus(sessionUser, mentoringStatus);
    return ResponseEntity.ok(ResponseType.of(changeStatusResult));
  }

}