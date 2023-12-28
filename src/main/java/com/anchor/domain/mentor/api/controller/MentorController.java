package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfos;
import com.anchor.domain.mentor.api.controller.request.MentoringUnavailableTimeInfos;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MentorController {

  private final MentorService mentorService;

  @PostMapping("/me/schedule")
  public ResponseEntity<String> registerUnavailableTime(
      @RequestBody MentoringUnavailableTimeInfos mentoringUnavailableTimeInfos,
      HttpSession httpSession) {

    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.setUnavailableTimes(user.getMentorId(),
        mentoringUnavailableTimeInfos.getDateTimeRanges());

    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<String> changeMentoringStatus(
      @RequestBody MentoringStatusInfos mentoringStatusInfos,
      HttpSession httpSession) {

    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.changeMentoringStatus(user.getMentorId(),
        mentoringStatusInfos.getMentoringStatusInfos());

    return ResponseEntity.ok()
        .build();
  }


}
