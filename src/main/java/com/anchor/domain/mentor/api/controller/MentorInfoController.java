package com.anchor.domain.mentor.api.controller;


import com.anchor.domain.mentor.api.controller.request.MentorContentsRequest;
import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.global.auth.SessionUser;
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
  public ResponseEntity<String> modifyInfo(@RequestBody MentorInfoRequest mentorInfoRequest, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.editInfo(sessionUser, mentorInfoRequest);
    return ResponseEntity.ok()
        .build();
  }

  @DeleteMapping
  public ResponseEntity<String> deleteMentor(HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.deleteMentors(sessionUser);
    return ResponseEntity.ok()
        .build();
  }

  @PutMapping("/introduction")
  public ResponseEntity<String> modifyIntroduction(@RequestBody MentorContentsRequest mentorContentsRequest,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorInfoService.editContents(sessionUser, mentorContentsRequest);
    return ResponseEntity.ok()
        .build();
  }

}