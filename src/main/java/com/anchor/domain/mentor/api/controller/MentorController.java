package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.mentor.api.controller.request.RandomCodeMaker;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
  private final HttpSession session;

  @PutMapping("/me/schedule")
  public ResponseEntity<String> editMentorSchedule(@RequestBody MentorOpenCloseTimes mentorOpenCloseTimes,
      HttpSession httpSession) {
//    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.setMentorSchedule(1L, mentorOpenCloseTimes);
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<String> changeMentoringStatus(@RequestBody MentoringStatusInfo mentoringStatusInfo,
      HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    mentorService.changeMentoringStatus(1L, mentoringStatusInfo.getRequiredMentoringStatusInfos());
    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/register/email/send")
  public ResponseEntity emailSend(String receiver) {
    String emailCode = RandomCodeMaker.makeRandomCode();
    session.setAttribute("ecode", emailCode);
    mailService.sendAuthMail(receiver, emailCode);
    return new ResponseEntity("success", HttpStatus.OK);
  }

  @PostMapping("/register/email/auth")
  public ResponseEntity emailVerify(String userEmailCode) {
    String emailCode = (String) session.getAttribute("ecode");
    log.info("auth session email code===" + emailCode);
    if (userEmailCode.equals(emailCode)) {
      return new ResponseEntity("success", HttpStatus.OK);
    } else {
      return new ResponseEntity("fail", HttpStatus.OK);
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
