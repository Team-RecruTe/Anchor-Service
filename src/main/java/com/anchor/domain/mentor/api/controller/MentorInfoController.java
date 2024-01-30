package com.anchor.domain.mentor.api.controller;


import com.anchor.domain.mentor.api.controller.request.MentorContentsRequest;
import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.MentorInfoService;
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
@RequestMapping("/mentors/me")
@RestController
public class MentorInfoController {

  private final MentorInfoService mentorInfoService;

  @PutMapping("/info")
  public ResponseEntity<ResponseType> modifyInfo(@RequestBody MentorInfoRequest mentorInfoRequest,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.editInfo(sessionUser, mentorInfoRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PutMapping("/introduction")
  public ResponseEntity<ResponseType> modifyIntroduction(@RequestBody MentorContentsRequest mentorContentsRequest,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.editContents(sessionUser, mentorContentsRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @DeleteMapping
  public ResponseEntity<ResponseType> deleteMentor(HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.deleteMentors(sessionUser);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

}