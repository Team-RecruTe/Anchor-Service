package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.controller.request.PayupMonthRange;
import com.anchor.domain.mentor.api.controller.request.RequiredMentorEmailInfo;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.CodeCreator;
import com.anchor.global.util.ResponseType;
import com.anchor.global.util.SessionKeyType;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @PostMapping("/register/email/send")
  public ResponseEntity<ResponseType> emailSend(@RequestBody @Valid RequiredMentorEmailInfo emailInfo,
      HttpSession session) {
    String emailCode = CodeCreator.createEmailAuthCode();
    session.setAttribute(SessionKeyType.ECODE.getKey(), emailCode);
    mailService.sendAuthMail(emailInfo.getReceiver(), emailCode);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PostMapping("/register/email/auth")
  public ResponseEntity<ResponseType> emailVerify(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = (String) session.getAttribute(SessionKeyType.ECODE.getKey());
    return ResponseEntity.ok()
        .body(ResponseType.of(emailInfo.isSameAs(emailCode)));
  }

  @PostMapping
  public ResponseEntity<ResponseType> registerProcess(@RequestBody @Valid MentorRegisterInfo mentorRegisterInfo,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorService.register(mentorRegisterInfo, sessionUser);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PutMapping("/me/schedule")
  public ResponseEntity<ResponseType> editMentorSchedule(@RequestBody MentorOpenCloseTimes mentorOpenCloseTimes,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorService.setMentorSchedule(sessionUser.getMentorId(), mentorOpenCloseTimes);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<ResponseType> changeMentoringStatus(@RequestBody MentoringStatusInfo mentoringStatusInfo,
      HttpSession session) {
//    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorService.changeMentoringStatus(1L, mentoringStatusInfo);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @GetMapping("/me/payup-info")
  public ResponseEntity<MentorPayupResult> getPayupResults(
      @ModelAttribute @Valid PayupMonthRange monthRange, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
//    MentorPayupResult payupInfos = mentorService.getMentorPayupResult(monthRange, sessionUser.getMentorId());
    MentorPayupResult payupInfos = mentorService.getMentorPayupResult(monthRange, 1L);
    return ResponseEntity.ok(payupInfos);
  }

}
