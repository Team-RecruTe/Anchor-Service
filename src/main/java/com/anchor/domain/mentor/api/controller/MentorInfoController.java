package com.anchor.domain.mentor.api.controller;


import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentor.api.service.MentorIntroductionService;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/mentors/me")
@RestController
public class MentorInfoController {

  private final MentorInfoService mentorInfoService;
  //private final MentorIntroductionService mentorIntroductionService;

  @PutMapping("/info")
  public ResponseEntity<String> changeInfo(@RequestBody MentorInfoRequest mentorInfoRequest, HttpSession httpSession){
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorInfoService.editInfo(user.getMentorId(), mentorInfoRequest);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping
  public ResponseEntity<String> deleteMentor(HttpSession httpSession){
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorInfoService.deleteMentors(user.getMentorId());
    return ResponseEntity.ok().build();
  }

  @PutMapping("/introduction")
  public ResponseEntity<String> putMentorsIntroduction(@RequestBody MentorIntroductionRequest mentorIntroductionRequest, HttpSession httpSession){
    //mentorIntroductionService.editContents(id, mentorIntroductionRequest);
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorInfoService.editIntroduction(user.getMentorId(), mentorIntroductionRequest);
    return ResponseEntity.ok().build();
  }

}
