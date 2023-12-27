package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.type.Link;
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
  public ResponseEntity<List<MentoringResponseDto>> mentoringList() {

    List<MentoringResponseDto> mentoringList = mentoringService.loadMentoringList();
    return ResponseEntity.ok()
        .body(mentoringList);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MentoringDetailResponseDto> mentoringDetail(
      @PathVariable("id") Long id) {

    MentoringDetailResponseDto mentoringDetail = mentoringService.loadMentoringDetail(id);

    return ResponseEntity.ok()
        .body(mentoringDetail);
  }


  @GetMapping("/{id}/apply")
  public ResponseEntity<List<MentoringUnavailableTimeDto>> mentoringApplicationPage(
      @PathVariable("id") Long id, HttpSession session) {

    List<MentoringUnavailableTimeDto> mentoringUnavailableTimes =
        mentoringService.loadMentoringUnavailableTime(id);

    List<MentoringUnavailableTimeDto> sessionSavedMentoringUnavailableTimeList = getSessionSavedMentoringUnavailableTimeList(
        session, id);

    mentoringUnavailableTimes.addAll(sessionSavedMentoringUnavailableTimeList);

    return ResponseEntity.ok()
        .body(mentoringUnavailableTimes);
  }


  @PostMapping("/{id}/progress")
  public ResponseEntity<List<MentoringUnavailableTimeDto>> mentoringTimeSessionSave(
      @PathVariable("id") Long id,
      @RequestBody MentoringApplicationTimeDto applicationTime, HttpSession session) {

    List<MentoringUnavailableTimeDto> sessionMentoringUnavailableTime = getSessionSavedMentoringUnavailableTimeList(
        session, id);

    addSessionRequestMentoringApplicationTime(sessionMentoringUnavailableTime, applicationTime);

    return ResponseEntity.ok()
        .body(sessionMentoringUnavailableTime);
  }


  private List<MentoringUnavailableTimeDto> getSessionSavedMentoringUnavailableTimeList(
      HttpSession session, Long id) {
    List<MentoringUnavailableTimeDto> sessionSavedmentoringTimeList =
        (List<MentoringUnavailableTimeDto>) session.getAttribute(String.valueOf(id));

    return sessionSavedmentoringTimeList == null ?
        new ArrayList<>() : sessionSavedmentoringTimeList;
  }

  private void addSessionRequestMentoringApplicationTime(
      List<MentoringUnavailableTimeDto> sessionList, MentoringApplicationTimeDto applicationTime) {

    MentoringUnavailableTimeDto mentoringUnavailableTimeDto = applicationTime.convertToMentoringUnavailableTimeDto();

    if (!sessionList.contains(mentoringUnavailableTimeDto)) {
      sessionList.add(mentoringUnavailableTimeDto);
    }
  }
}
