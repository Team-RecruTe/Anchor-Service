package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationUserInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.ApplicationTimeInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringPaymentInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSaveRequestInfo;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.Link;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mentorings")
@RestController
public class MentoringController {

  private static final String SUCCESS = "success";
  private final MentoringService mentoringService;

  @PostMapping
  public ResponseEntity<MentoringCreateResult> createMentoring(
      @RequestBody @Valid MentoringBasicInfo mentoringBasicInfo, HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentoringCreateResult result = mentoringService.create(user.getMentorId(),
        mentoringBasicInfo);

    result.addLinks(Link.builder()
        .setLink("self", String.format("/mentorings/%d", result.getId()))
        .build());

    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MentoringEditResult> editMentoring(@PathVariable Long id,
      @RequestBody @Valid MentoringBasicInfo mentoringBasicInfo) {
    MentoringEditResult result = mentoringService.edit(id, mentoringBasicInfo);
    result.addLinks(Link.builder()
        .setLink("self", String.format("/mentorings/%d", result.getId()))
        .build());

    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MentoringDeleteResult> deleteMentoring(@PathVariable Long id) {
    MentoringDeleteResult result = mentoringService.delete(id);
    return ResponseEntity.ok(result);
  }

  @PutMapping("/{id}/contents")
  public ResponseEntity<MentoringContentsEditResult> editContents(@PathVariable Long id,
      @RequestBody @Valid MentoringContentsInfo mentoringContentsInfo) {
    MentoringContentsEditResult result = mentoringService.editContents(id, mentoringContentsInfo);
    result.addLinks(Link.builder()
        .setLink("self", String.format("/mentorings/%d/contents", result.getId()))
        .build());
    return ResponseEntity.ok(result);
  }

  @GetMapping("/rank")
  public ResponseEntity<TopMentoring> getTopMentoring() {
    TopMentoring result = mentoringService.getTopMentorings();
    result.addLinks(Link.builder()
        .setLink("self", "/mentorings/rank")
        .build());
    return ResponseEntity.ok(result);
  }

  /**
   * 멘토의 활동시간과 해당 멘토링의 신청내역, Redis에 저장된 결제중인 멘토링시간대를 조회합니다. 해당 멘토링의 신청대기 또는 완료된 시간대를 조회한 후 활동시간과 신청내역 시간을 분리해서 클라이언트로
   * 전달합니다.
   */
  @GetMapping("/{id}/reservation-time")
  public ResponseEntity<ApplicationTimeInfo> mentoringActiveTime(@PathVariable("id") Long id) {
    ApplicationTimeInfo mentoringActiveTimes = mentoringService.getMentoringActiveTimes(id);
    return ResponseEntity.ok()
        .body(mentoringActiveTimes);
  }

  /**
   * 신청중인 멘토링 시간을 잠금처리하고, 세션에 멘토링 신청중인 데이터를 저장합니다.
   */
  @PostMapping("/{id}/lock")
  public ResponseEntity<String> applicationTimeLock(@PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentoringService.lock(id, sessionUser, applicationTime);
    return ResponseEntity.ok()
        .body(SUCCESS);
  }

  /**
   * 결제중인 멘토링 시간대의 잠금 유효시간을 갱신합니다.
   */
  @PutMapping("/{id}/refresh")
  public ResponseEntity<String> refreshPaymentTime(@PathVariable("id") Long id, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    boolean refreshResult = mentoringService.refresh(id, sessionUser);
    if (refreshResult) {
      return ResponseEntity.ok()
          .build();
    } else {
      return ResponseEntity.badRequest()
          .body("이미 예약시간이 만료되었습니다. 홈페이지로 이동합니다.");
    }
  }

  /**
   * 멘토링 신청 도중 페이지를 벗어나거나, 시간이 만료되면 잠금을 해제합니다.
   */
  @DeleteMapping("/{id}/unlock")
  public ResponseEntity<String> mentoringTimeSessionRemove(@PathVariable("id") Long id, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    mentoringService.unlock(id, sessionUser);
    return ResponseEntity.ok()
        .body(SUCCESS);
  }

  /**
   * 포트원 결제 API를 실행하기 위한 데이터를 응답데이터로 반환합니다.
   */
  @PostMapping("/{id}/payment-process")
  public ResponseEntity<MentoringPaymentInfo> mentoringTimeSessionSave(
      @PathVariable("id") Long id, @RequestBody MentoringApplicationUserInfo userInfo, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    MentoringPaymentInfo mentoringPaymentInfo = mentoringService.createPaymentInfo(id, userInfo, sessionUser);
    return ResponseEntity.ok()
        .body(mentoringPaymentInfo);
  }

  /**
   * 멘토링 결제 완료가 되면 멘토링 신청이력을 저장합니다.
   */
  @PostMapping("/{id}/apply")
  public ResponseEntity<MentoringSaveRequestInfo> mentoringApplicationSave
  (@PathVariable("id") Long id, @RequestBody MentoringApplicationInfo applicationInfo, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    MentoringSaveRequestInfo mentoringSaveRequestInfo =
        mentoringService.saveMentoringApplication(sessionUser, id, applicationInfo);
    if (mentoringSaveRequestInfo != null) {
      return ResponseEntity.ok()
          .body(mentoringSaveRequestInfo);
    }
    return ResponseEntity.badRequest()
        .build();
  }

}