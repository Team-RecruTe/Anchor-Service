package com.anchor.domain.mentor.api.controller;


import com.anchor.domain.mentor.api.controller.request.MentorContentsRequest;
import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.ResponseType;
import jakarta.validation.Valid;
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
@RequestMapping("/mentors/me")
@RestController
public class MentorInfoController {

  private final MentorInfoService mentorInfoService;

  /**
   * 멘토 정보를 편집합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @PutMapping("/info")
  public ResponseEntity<ResponseType> modifyInfo(@RequestBody @Valid MentorInfoRequest mentorInfoRequest,
      @SessionAttribute("user") SessionUser user) {
    mentorInfoService.editInfo(user, mentorInfoRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 멘토 소개글을 편집합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @PutMapping("/introduction")
  public ResponseEntity<ResponseType> modifyIntroduction(@RequestBody MentorContentsRequest mentorContentsRequest,
      @SessionAttribute("user") SessionUser user) {
    mentorInfoService.editContents(user, mentorContentsRequest);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 멘토를 삭제합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @DeleteMapping
  public ResponseEntity<ResponseType> deleteMentor(@SessionAttribute("user") SessionUser user) {
    mentorInfoService.deleteMentors(user);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

}