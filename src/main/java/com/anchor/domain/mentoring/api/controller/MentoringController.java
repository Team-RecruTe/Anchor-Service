package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringApplicationResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.Link;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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



  @GetMapping("")
  public ResponseEntity<List<MentoringInfo>> mentoringList() {

    List<MentoringInfo> mentoringInfoList = mentoringService.loadMentoringList();

    return ResponseEntity.ok()
        .body(mentoringInfoList);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MentoringDetailResponse> mentoringDetail(
      @PathVariable("id") Long id) {

    MentoringDetailResponse mentoringDetailResponse = mentoringService.loadMentoringDetail(id);

    return ResponseEntity.ok()
        .body(mentoringDetailResponse);
  }


  @GetMapping("/{id}/apply")
  public ResponseEntity<List<MentoringUnavailableTimeResponse>> mentoringApplicationPage(
      @PathVariable("id") Long id, HttpSession session) {

    List<MentoringUnavailableTimeResponse> mentoringUnavailableTimes =
        mentoringService.loadMentoringUnavailableTime(id);

    List<MentoringUnavailableTimeResponse> sessionSavedMentoringUnavailableTimeList =
        getSessionSavedMentoringUnavailableTimeList(session, id);

    mentoringUnavailableTimes.addAll(sessionSavedMentoringUnavailableTimeList);

    updateSessionSavedMentoringUnavailableTimeList(session, id, mentoringUnavailableTimes);
    return ResponseEntity.ok()
        .body(mentoringUnavailableTimes);
  }

  /**
   * 멘토링 결제 완료가 되면 멘토링 신청이력을 저장합니다.
   */
  @PostMapping("/{id}/apply")
  public ResponseEntity<MentoringApplicationResponse> mentoringApplicationSave(
      @PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {

    SessionUser sessionUser = (SessionUser) session.getAttribute("user");

    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 없습니다. 잘못된 접근입니다.");
    }

    MentoringApplicationResponse mentoringApplicationResponse = mentoringService.saveMentoringApplication(
        sessionUser, id,
        applicationTime);

    if (mentoringApplicationResponse != null) {

      List<MentoringUnavailableTimeResponse> sessionSavedMentoringUnavailableTimeList =
          getSessionSavedMentoringUnavailableTimeList(session, id);

      mentoringService.removeMentoringApplicationTimeFromSession
          (sessionSavedMentoringUnavailableTimeList, applicationTime);

      updateSessionSavedMentoringUnavailableTimeList(session, id,
          sessionSavedMentoringUnavailableTimeList);
      return ResponseEntity.ok()
          .body(mentoringApplicationResponse);
    }

    return ResponseEntity.badRequest()
        .build();
  }

  /**
   * 멘토링 신청과정입니다. 결제진행중인 시간대를 다른 회원이 신청하지 못하도록 잠금처리 합니다.
   */
  @PostMapping("/{id}/apply-process")
  public String mentoringTimeSessionSave(
      @PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {

    List<MentoringUnavailableTimeResponse> sessionSavedMentoringUnavailableTimeList = getSessionSavedMentoringUnavailableTimeList(
        session, id);

    mentoringService.addMentoringApplicationTimeFromSession(
        sessionSavedMentoringUnavailableTimeList,
        applicationTime);

    updateSessionSavedMentoringUnavailableTimeList(session, id,
        sessionSavedMentoringUnavailableTimeList);
    return SUCCESS;
  }

  /**
   * 멘토링 신청 도중 결제실패 또는 취소시 잠금상태였던 시간대를 해제합니다.
   */
  @PostMapping("/{id}/apply-cancel")
  public String mentoringTimeSessionRemove(@PathVariable("id") Long id,
      @RequestBody MentoringApplicationTime applicationTime, HttpSession session) {

    List<MentoringUnavailableTimeResponse> sessionSavedMentoringUnavailableTimeList =
        getSessionSavedMentoringUnavailableTimeList(session, id);

    boolean removeResult = mentoringService.removeMentoringApplicationTimeFromSession
        (sessionSavedMentoringUnavailableTimeList, applicationTime);

    updateSessionSavedMentoringUnavailableTimeList(session, id,
        sessionSavedMentoringUnavailableTimeList);
    return removeResult ? SUCCESS : FAILURE;
  }


  private List<MentoringUnavailableTimeResponse> getSessionSavedMentoringUnavailableTimeList(
      HttpSession session, Long id) {
    List<MentoringUnavailableTimeResponse> sessionSavedmentoringTimeList =
        (List<MentoringUnavailableTimeResponse>) session.getAttribute(String.valueOf(id));

    return sessionSavedmentoringTimeList == null ?
        new ArrayList<>() : sessionSavedmentoringTimeList;
  }

  private void updateSessionSavedMentoringUnavailableTimeList(HttpSession session, Long id,
      List<MentoringUnavailableTimeResponse> sessionSavedTimeList) {
    session.setAttribute(String.valueOf(id), sessionSavedTimeList);
  }
}
