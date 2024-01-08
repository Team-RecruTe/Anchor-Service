package com.anchor.domain.user.api.controller;

import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
   *  유저 정보 변경
   */
  @PutMapping("/me")
  public ResponseEntity<String> putInfo(@RequestBody UserNicknameRequest userNicknameRequest, HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user");
    userService.editNickname(sessionUser.getEmail(), userNicknameRequest);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/me/image")
  public ResponseEntity<String> putImage(@RequestBody UserNicknameRequest userNicknameRequest, HttpSession httpSession){
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/me")
  public ResponseEntity<String> deleteUser(HttpSession httpSession){
    SessionUser sessionUser = (SessionUser) httpSession.getAttribute("user"); //email, nickname, image
    userService.deleteUser(sessionUser.getEmail());
    return ResponseEntity.ok().build();
  }


  /**
   * 멘토링 신청내역을 조회합니다.
   */
  @GetMapping("/me/applied-mentorings")
  public ResponseEntity<List<AppliedMentoringInfo>> appliedMentoringList(HttpSession session) {

    SessionUser sessionUser = getSessionUserFromSession(session);

    List<AppliedMentoringInfo> appliedMentoringInfoList = userService.loadAppliedMentoringList(
        sessionUser);

    return ResponseEntity.ok()
        .body(appliedMentoringInfoList);
  }


  /**
   * 신청한 멘토링의 상태를 변경합니다. 취소, 또는 완료로 변경가능합니다.
   */
  @PutMapping("/me/applied-mentorings")
  public String appliedMentoringStatusChange(@RequestBody MentoringStatusInfo mentoringStatus,
      HttpSession session) {

    SessionUser sessionUser = getSessionUserFromSession(session);

    return userService.changeAppliedMentoringStatus(sessionUser, mentoringStatus) ?
        SUCCESS : FAILURE;
  }


  private SessionUser getSessionUserFromSession(HttpSession session) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 없습니다. 잘못된 접근입니다.");
    }
    return sessionUser;
  }


}
