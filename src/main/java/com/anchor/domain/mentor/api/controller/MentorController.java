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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@Slf4j
@RequestMapping("/mentors")
@RequiredArgsConstructor
@RestController
public class MentorController {

  private final MentorService mentorService;
  private final MailService mailService;

  /**
   * 멘토 인증 이메일을 발송합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PostMapping("/register/email/send")
  public ResponseEntity<ResponseType> emailSend(@RequestBody @Valid RequiredMentorEmailInfo emailInfo,
      HttpSession session) {
    String emailCode = CodeCreator.createEmailAuthCode();
    session.setAttribute(SessionKeyType.ECODE.getKey(), emailCode);
    mailService.sendAuthMail(emailInfo.getReceiver(), emailCode);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 멘토 인증코드를 처리합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PostMapping("/register/email/auth")
  public ResponseEntity<ResponseType> emailVerify(@RequestBody RequiredMentorEmailInfo emailInfo, HttpSession session) {
    String emailCode = (String) session.getAttribute(SessionKeyType.ECODE.getKey());
    return ResponseEntity.ok()
        .body(ResponseType.of(emailInfo.isSameAs(emailCode)));
  }

  /**
   * 멘토를 등록합니다.
   */
  @PreAuthorize("hasRole('ROLE_USER')")
  @PostMapping
  public ResponseEntity<ResponseType> registerProcess(@RequestBody @Valid MentorRegisterInfo mentorRegisterInfo,
      @SessionAttribute("user") SessionUser user) {
    mentorService.register(mentorRegisterInfo, user);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 멘토 스케줄을 편집합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @PutMapping("/me/schedule")
  public ResponseEntity<ResponseType> editMentorSchedule(@RequestBody MentorOpenCloseTimes mentorOpenCloseTimes,
      @SessionAttribute("user") SessionUser user) {
    mentorService.setMentorSchedule(user.getMentorId(), mentorOpenCloseTimes);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 신청된 멘토링 상태를 변경합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @PostMapping("/me/applied-mentorings")
  public ResponseEntity<ResponseType> changeMentoringStatus(@RequestBody MentoringStatusInfo mentoringStatusInfo,
      @SessionAttribute("user") SessionUser user) {
    mentorService.changeMentoringStatus(user.getMentorId(), mentoringStatusInfo);
    return ResponseEntity.ok(ResponseType.SUCCESS);
  }

  /**
   * 멘토 정산 정보를 조회합니다.
   */
  @PreAuthorize("hasRole('ROLE_MENTOR')")
  @GetMapping("/me/payup-info")
  public ResponseEntity<MentorPayupResult> getPayupResults(
      @ModelAttribute @Valid PayupMonthRange monthRange, @SessionAttribute("user") SessionUser user) {
    MentorPayupResult payupInfos = mentorService.getMentorPayupResult(monthRange, user.getMentorId());
    return ResponseEntity.ok(payupInfos);
  }

}
