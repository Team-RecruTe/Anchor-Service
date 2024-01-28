package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.mentor.api.controller.request.RequiredMentorEmailInfo;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.CodeCreator;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/mentors")
@RequiredArgsConstructor
@RestController
public class MentorController {

  private final MentorService mentorService;
  private final MailService mailService;

  @PutMapping("/me/schedule")
  public ResponseEntity<String> editMentorSchedule(@RequestBody MentorOpenCloseTimes mentorOpenCloseTimes,
      HttpSession session) {
//    SessionUser user = SessionUser.getSessionUser(session);
    mentorService.setMentorSchedule(1L, mentorOpenCloseTimes);
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<String> changeMentoringStatus(@RequestBody MentoringStatusInfo mentoringStatusInfo,
      HttpSession session) {
    SessionUser user = SessionUser.getSessionUser(session);
    mentorService.changeMentoringStatus(1L, mentoringStatusInfo.getRequiredMentoringStatusInfos());
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/register/email/send")
  public ResponseEntity<String> emailSend(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = CodeCreator.createEmailAuthCode();
    session.setAttribute("ecode", emailCode);
    mailService.sendAuthMail(emailInfo.getReceiver(), emailCode);
    return ResponseEntity.ok()
        .body("success");
  }

  @PostMapping("/register/email/auth")
  public ResponseEntity<String> emailVerify(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = (String) session.getAttribute("ecode");
    log.info("auth session email code===" + emailCode);
    if (emailInfo.getUserEmailCode()
        .equals(emailCode)) {
      return ResponseEntity.ok()
          .body("success");
    } else {
      return ResponseEntity.badRequest()
          .body("fail");
    }
  }

  @GetMapping("/me/payup-info")
  public ResponseEntity<MentorPayupResult> getTest(
      @RequestParam("currentMonth") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime currentMonth,
      @RequestParam("startMonth") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startMonth
      /*,HttpSession session*/) {
    SessionUser sessionUser = new SessionUser();
    if (isFutureDate(currentMonth)) {
      return ResponseEntity.ok(new MentorPayupResult());
    }
    MentorPayupResult payupInfos = mentorService.getMentorPayupResult(startMonth, currentMonth, sessionUser);
    return ResponseEntity.ok(payupInfos);
  }

  private boolean isFutureDate(LocalDateTime currentMonth) {
    return currentMonth.isAfter(LocalDateTime.now()
        .with(TemporalAdjusters.lastDayOfMonth()));
  }
}
