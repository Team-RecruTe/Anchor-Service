package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.ApplicationUnavailableTime;
import com.anchor.domain.mentoring.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringPaymentInfo;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.global.auth.SessionUser;
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
  private static final String FAILURE = "failure";
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
   * 멘토링 신청페이지로 이동합니다. 응답으로 멘토링 불가시간을 클라이언트로 전달합니다.
   */
/*  @GetMapping("/{id}/apply")
  public ResponseEntity<Set<ApplicationUnavailableTime>> mentoringApplicationPage(
      @PathVariable("id") Long id, HttpSession session) {
    Set<ApplicationUnavailableTime> unavailableTimes = mentoringService.getMentoringUnavailableTimes(id);
    Set<ApplicationUnavailableTime> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);
    sessionUnavailableTimes.addAll(unavailableTimes);
    updateSessionUnavailableTimes(session, id, sessionUnavailableTimes);
    return ResponseEntity.ok()
        .body(sessionUnavailableTimes);
  }*/

  /**
   * 멘토링 결제 완료가 되면 멘토링 신청이력을 저장합니다.
   */
  @PostMapping("/{id}/apply")
  public ResponseEntity<AppliedMentoringInfo> mentoringApplicationSave
  (@PathVariable("id") Long id, @RequestBody MentoringApplicationInfo applicationInfo, HttpSession session) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 없습니다. 잘못된 접근입니다.");
    }
    AppliedMentoringInfo appliedMentoringInfo =
        mentoringService.saveMentoringApplication(sessionUser, id, applicationInfo);

    if (appliedMentoringInfo != null) {
      Set<ApplicationUnavailableTime> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);
      mentoringService.removeApplicationTimeFromSession(sessionUnavailableTimes, applicationInfo);
      updateSessionUnavailableTimes(session, id, sessionUnavailableTimes);
      return ResponseEntity.ok()
          .body(appliedMentoringInfo);
    }

    return ResponseEntity.badRequest()
        .build();
  }

  /**
   * 멘토링 신청과정입니다. 결제진행중인 시간대를 다른 회원이 신청하지 못하도록 잠금처리 합니다.
   */
  @PostMapping("/{id}/apply-process")
  public ResponseEntity<MentoringPaymentInfo> mentoringTimeSessionSave(
      @PathVariable("id") Long id, @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {
    Set<ApplicationUnavailableTime> sessionUnavailableTimes = getSessionUnavailableTimes(session, id);
    mentoringService.addApplicationTimeFromSession(sessionUnavailableTimes, applicationTime);
    MentoringPaymentInfo mentoringPaymentInfo = mentoringService.createPaymentInfo(id, applicationTime);
    updateSessionUnavailableTimes(session, id, sessionUnavailableTimes);
    return ResponseEntity.ok()
        .body(mentoringPaymentInfo);
  }

  /**
   * 멘토링 신청 도중 결제실패 또는 취소시 잠금상태였던 시간대를 해제합니다.
   */
  @PutMapping("/{id}/apply-cancel")
  public String mentoringTimeSessionRemove(@PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {
    Set<ApplicationUnavailableTime> sessionApplicationUnavailableTimeList = getSessionUnavailableTimes(session, id);
    boolean removeResult = mentoringService.removeApplicationTimeFromSession(sessionApplicationUnavailableTimeList,
        applicationTime);
    updateSessionUnavailableTimes(session, id, sessionApplicationUnavailableTimeList);
    return removeResult ? SUCCESS : FAILURE;
  }

  private Set<ApplicationUnavailableTime> getSessionUnavailableTimes(HttpSession session, Long id) {
    Object sessionUnavailableTime = session.getAttribute(String.valueOf(id));
    if (Objects.nonNull(sessionUnavailableTime)) {
      return (Set<ApplicationUnavailableTime>) sessionUnavailableTime;
    }
    return new HashSet<>();
  }

  private void updateSessionUnavailableTimes(HttpSession session, Long id,
      Set<ApplicationUnavailableTime> sessionUnavailableTimes) {
    session.setAttribute(String.valueOf(id), sessionUnavailableTimes);
  }

}