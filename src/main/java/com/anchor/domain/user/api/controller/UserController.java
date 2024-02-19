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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  /**
   * 유저 닉네임을 변경합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PutMapping("/me")
  public ResponseEntity<ResponseType> putInfo(@RequestBody UserNicknameRequest userNicknameRequest,
      @SessionAttribute("user") SessionUser user) {
    userService.editNickname(user, userNicknameRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 유저 프로필 이미지를 변경합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PutMapping("/me/image")
  public ResponseEntity<ResponseType> putImage(@RequestBody UserImageRequest userImageRequest,
      @SessionAttribute("user") SessionUser user) {
    userService.uploadImage(user, userImageRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 유저를 삭제합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @DeleteMapping("/me")
  public ResponseEntity<ResponseType> deleteUser(HttpSession session) {
    SessionUser user = (SessionUser) session.getAttribute("user");
    userService.deleteUser(user);
    session.invalidate();
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }


  /**
   * 신청한 멘토링의 상태를 변경합니다. 취소, 또는 완료로 변경가능합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PutMapping("/me/applied-mentorings")
  public ResponseEntity<ResponseType> appliedMentoringStatusChange(@RequestBody MentoringStatusInfo mentoringStatus,
      @SessionAttribute("user") SessionUser user) {
    boolean changeStatusResult = userService.changeMentoringStatus(user, mentoringStatus);
    return ResponseEntity.ok(ResponseType.of(changeStatusResult));
  }

}