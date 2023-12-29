package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MailDto;
import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.mentor.api.controller.request.MentoringUnavailableTimeInfo;
import com.anchor.domain.mentor.api.controller.request.RandomCodeMaker;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.MentoringUnavailableTimes;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.Link;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/mentors")
@RequiredArgsConstructor
@RestController
public class MentorController {

  private final MentorService mentorService;
  private final MailService mailService;
  private final HttpSession session;

  @GetMapping("/me/schedule")
  public ResponseEntity<MentoringUnavailableTimes> getUnavailableTimes(HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentoringUnavailableTimes result = mentorService.getUnavailableTimes(user.getMentorId());
    result.addLinks(Link.builder()
        .setLink("self", "/me/schedule")
        .build());
    return ResponseEntity.ok(result);
  }

  @PostMapping("/me/schedule")
  public ResponseEntity<String> registerUnavailableTimes(
      @Valid @RequestBody MentoringUnavailableTimeInfo mentoringUnavailableTimeInfo, HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.setUnavailableTimes(user.getMentorId(), mentoringUnavailableTimeInfo.getDateTimeRanges());
    log.info("IsEmpty: {}", mentoringUnavailableTimeInfo.getDateTimeRanges()
        .isEmpty());
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<String> changeMentoringStatus(
      @RequestBody MentoringStatusInfo mentoringStatusInfo,
      HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.changeMentoringStatus(user.getMentorId(), mentoringStatusInfo.getRequiredMentoringStatusInfos());
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/register/email/send")
  public Map<String, Boolean> emailSend(String receiver) {
    String emailCode = RandomCodeMaker.makeRandomCode();
    session.setAttribute("ecode", emailCode);
    MailDto mailDto = MailDto.builder()
        .receiver(receiver)
        .title("Anchor-Service: 이메일 인증키입니다.")
        .content(emailCode)
        .build();
    mailService.sendMail(mailDto);
    log.info("new session email code===" + emailCode);
    Map<String, Boolean> resultMap = new HashMap<>();
    resultMap.put("isSent", true);
    return resultMap;
  }

  @PostMapping("/register/email/auth")
  public String emailVerify(String userEmailCode) {
    String emailCode = (String) session.getAttribute("ecode");
    if (userEmailCode.equals(emailCode)) {
      return "이메일 인증 성공";
    } else {
      return "이메일 인증 실패";
    }
  }

}
