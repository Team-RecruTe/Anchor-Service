package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationUserInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.ApplicationTimeInfo;
import com.anchor.domain.mentoring.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringPaymentInfo;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.DateTimeRange;
import com.anchor.global.util.type.Link;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
   * 멘토의 활동시간과 해당 멘토링의 신청내역, 세션에 저장된 결제중인 멘토링시간대를 조회합니다. 해당 멘토링의 신청대기 또는 완료된 시간대를 조회한 후 활동시간과 신청내역 시간을 분리해서 클라이언트로
   * 전달합니다.
   */
  @GetMapping("/{id}/reservation-time")
  public ResponseEntity<ApplicationTimeInfo> mentoringActiveTime(@PathVariable("id") Long id, HttpSession session) {

    ApplicationTimeInfo mentoringActiveTimes = mentoringService.getMentoringActiveTimes(id);

    Set<DateTimeRange> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);

    sessionUnavailableTimes.forEach(mentoringActiveTimes::add);
    updateSessionUnavailableTime(session, id, sessionUnavailableTimes);
    return ResponseEntity.ok()
        .body(mentoringActiveTimes);
  }

  /**
   * 신청중인 멘토링 시간을 잠금처리하고, 세션에 멘토링 신청중인 데이터를 저장합니다.
   */
  @PostMapping("/{id}/lock")
  public ResponseEntity<String> applicationTimeLock(@PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {

    Set<DateTimeRange> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);

    mentoringService.addApplicationTimeFromSession(sessionUnavailableTimes, applicationTime);
    updateSessionUnavailableTime(session, id, sessionUnavailableTimes);

    saveMentoringTimeInSession(session, applicationTime);

    return ResponseEntity.ok()
        .body(SUCCESS);
  }

  /**
   * 멘토링 신청 도중 결제실패 또는 취소시 잠금상태였던 시간대를 해제합니다.
   */
  @PutMapping("/{id}/unlock")
  public ResponseEntity<String> mentoringTimeSessionRemove(@PathVariable("id") Long id, HttpSession session) {
    Set<DateTimeRange> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);
    DateTimeRange myAppliedMentoringTimeRange = getMyAppliedMentoringTimeRange(session);
    mentoringService.removeApplicationTimeFromSession(sessionUnavailableTimes, myAppliedMentoringTimeRange);
    updateSessionUnavailableTime(session, id, sessionUnavailableTimes);

    return ResponseEntity.ok()
        .body(SUCCESS);
  }

  /**
   * 포트원 결제 API를 실행하기 위한 데이터를 응답데이터로 반환합니다.
   */
  @PostMapping("/{id}/payment-process")
  public ResponseEntity<MentoringPaymentInfo> mentoringTimeSessionSave(
      @PathVariable("id") Long id, @RequestBody MentoringApplicationUserInfo userInfo, HttpSession session) {
    DateTimeRange myAppliedMentoringTimeRange = getMyAppliedMentoringTimeRange(session);
    MentoringPaymentInfo mentoringPaymentInfo = mentoringService.createPaymentInfo(id, myAppliedMentoringTimeRange,
        userInfo);
    return ResponseEntity.ok()
        .body(mentoringPaymentInfo);
  }

  /**
   * 멘토링 결제 완료가 되면 멘토링 신청이력을 저장합니다.
   */
  @PostMapping("/{id}/apply")
  public ResponseEntity<AppliedMentoringInfo> mentoringApplicationSave
  (@PathVariable("id") Long id, @RequestBody MentoringApplicationInfo applicationInfo, HttpSession session) {
    SessionUser sessionUser = getSessionUser(session);

    DateTimeRange myAppliedMentoringTimeRange = getMyAppliedMentoringTimeRange(session);

    applicationInfo.addApplicationTime(myAppliedMentoringTimeRange);

    AppliedMentoringInfo appliedMentoringInfo =
        mentoringService.saveMentoringApplication(sessionUser, id, applicationInfo);

    if (appliedMentoringInfo != null) {
      Set<DateTimeRange> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);
      mentoringService.removeApplicationTimeFromSession(sessionUnavailableTimes, myAppliedMentoringTimeRange);

      return ResponseEntity.ok()
          .body(appliedMentoringInfo);
    }

    return ResponseEntity.badRequest()
        .build();
  }

  private SessionUser getSessionUser(HttpSession session) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 존재하지 않습니다.");
    }
    return sessionUser;
  }

  private Set<DateTimeRange> getSessionUnavailableTimes(HttpSession session, Long id) {
    Object attribute = session.getAttribute(String.valueOf(id));
    if (Objects.nonNull(attribute)) {
      return (Set<DateTimeRange>) attribute;
    }
    return new HashSet<>();
  }

  private void updateSessionUnavailableTime(HttpSession session, Long id,
      Set<DateTimeRange> sessionUnavailableTimes) {
    session.setAttribute(String.valueOf(id), sessionUnavailableTimes);
  }

  private void saveMentoringTimeInSession(HttpSession session, MentoringApplicationTime applicationTime) {
    SessionUser sessionUser = getSessionUser(session);
    session.setAttribute(sessionUser.getEmail(), applicationTime.convertDateTimeRange());
  }

  private DateTimeRange getMyAppliedMentoringTimeRange(HttpSession session) {
    SessionUser sessionUser = getSessionUser(session);
    Object attribute = session.getAttribute(sessionUser.getEmail());
    if (Objects.nonNull(attribute)) {
      return (DateTimeRange) attribute;
    } else {
      throw new RuntimeException("신청중이던 시간이 존재하지 않습니다.");
    }
  }

}