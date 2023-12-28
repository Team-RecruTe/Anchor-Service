package com.anchor.domain.mentoring.api.controller;

import static com.anchor.domain.user.domain.UserRole.MENTOR;

import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContents;
import com.anchor.domain.mentoring.api.controller.request.MentoringUnavailableTimeInfos;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringCreationResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.user.domain.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
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
  public ResponseEntity<MentoringCreationResult> createMentoring(
      @RequestBody @Valid MentoringBasicInfo mentoringBasicInfo, HttpSession httpSession) {
    User user = (User) httpSession.getAttribute("user");
    if (user.getRole() != MENTOR) {
      throw new AuthorizationServiceException("멘토링을 생성할 권한이 없습니다.");
    }

    MentoringCreationResult mentoringCreationResult = mentoringService.create(user.getMentor(),
        mentoringBasicInfo);

    return ResponseEntity.ok(mentoringCreationResult);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MentoringEditResult> editMentoring(@PathVariable Long id,
      @RequestBody @Valid MentoringBasicInfo mentoringBasicInfo, HttpSession httpSession) {
    User user = (User) httpSession.getAttribute("user");
    if (user.getRole() != MENTOR) {
      throw new AuthorizationServiceException("멘토링을 수정할 권한이 없습니다.");
    }

    MentoringEditResult mentoringEditResult = mentoringService.edit(id, mentoringBasicInfo);

    return ResponseEntity.ok(mentoringEditResult);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteMentoring(@PathVariable Long id, HttpSession httpSession) {
    User user = (User) httpSession.getAttribute("user");
    if (user.getRole() != MENTOR) {
      throw new AuthorizationServiceException("멘토링을 삭제할 권한이 없습니다.");
    }

    mentoringService.delete(id);

    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/{id}/schedule")
  public ResponseEntity<String> registerUnavailableTime(@PathVariable Long id,
      @RequestBody MentoringUnavailableTimeInfos mentoringUnavailableTimeInfos) {

    mentoringService.setUnavailableTimes(id, mentoringUnavailableTimeInfos.getDateTimeRanges());

    return ResponseEntity.ok()
        .build();
  }

  @PostMapping("/{id}/contents")
  public ResponseEntity<String> registerMentoringDetail(@PathVariable Long id,
      @RequestBody MentoringContents mentoringContents) {
    mentoringService.registerContents(id, mentoringContents);

    return ResponseEntity.ok()
        .build();
  }

  @PutMapping("/{id}/contents")
  public ResponseEntity<String> editMentoringDetail(@PathVariable Long id,
      @RequestBody MentoringContents mentoringContents) {
    mentoringService.editContents(id, mentoringContents);

    return ResponseEntity.ok()
        .build();
  }

}
