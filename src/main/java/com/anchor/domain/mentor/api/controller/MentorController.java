package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.mentor.api.controller.request.RequiredMentorEmailInfo;
import com.anchor.domain.mentor.api.service.MailService;
import com.anchor.domain.mentor.api.service.MentorService;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.type.mentor.FutureDateException;
import com.anchor.global.util.CodeCreator;
import com.anchor.global.util.ResponseType;
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

  @PostMapping("/register/email/send")
  public ResponseEntity<ResponseType> emailSend(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = CodeCreator.createEmailAuthCode();
    session.setAttribute("ecode", emailCode);
    mailService.sendAuthMail(emailInfo.getReceiver(), emailCode);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PostMapping("/register/email/auth")
  public ResponseEntity<ResponseType> emailVerify(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = (String) session.getAttribute("ecode");
    return ResponseEntity.ok()
        .body(ResponseType.of(emailInfo.isSameAs(emailCode)));
  }

  @PostMapping
  public ResponseEntity<ResponseType> registerProcess(@RequestBody MentorRegisterInfo mentorRegisterInfo,
      HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentorService.register(mentorRegisterInfo, sessionUser);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PutMapping("/me/schedule")
  public ResponseEntity<ResponseType> editMentorSchedule(@RequestBody MentorOpenCloseTimes mentorOpenCloseTimes,
      HttpSession session) {
//    SessionUser user = SessionUser.getSessionUser(session);
    mentorService.setMentorSchedule(1L, mentorOpenCloseTimes);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<ResponseType> changeMentoringStatus(@RequestBody MentoringStatusInfo mentoringStatusInfo,
      HttpSession session) {
    SessionUser user = SessionUser.getSessionUser(session);
    mentorService.changeMentoringStatus(1L, mentoringStatusInfo.getRequiredMentoringStatusInfos());
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  @GetMapping("/me/payup-info")
  public ResponseEntity<MentorPayupResult> getTest(
      @RequestParam("currentMonth") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime currentMonth,
      @RequestParam("startMonth") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startMonth
      /*,HttpSession session*/) {
    SessionUser sessionUser = new SessionUser();
    if (isFutureDate(currentMonth)) {
      throw new FutureDateException();
    }
    MentorPayupResult payupInfos = mentorService.getMentorPayupResult(startMonth, currentMonth, sessionUser);
    return ResponseEntity.ok(payupInfos);
  }

  private boolean isFutureDate(LocalDateTime currentMonth) {
    return currentMonth.isAfter(LocalDateTime.now()
        .with(TemporalAdjusters.lastDayOfMonth()));
  }

}
