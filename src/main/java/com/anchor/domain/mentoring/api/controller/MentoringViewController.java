package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringViewService;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorings")
public class MentoringViewController {

  private final MentoringViewService mentoringViewService;

  @GetMapping("")
  public ResponseEntity<List<MentoringResponseDto>> mentoringViewList() {

    List<MentoringResponseDto> mentoringList = mentoringViewService.loadMentoringList();
    return ResponseEntity.ok()
        .body(mentoringList);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MentoringDetailResponseDto> viewMentoringDetail(
      @PathVariable("id") Long id) {

    MentoringDetailResponseDto mentoringDetail = mentoringViewService.loadMentoringDetail(id);

    return ResponseEntity.ok()
        .body(mentoringDetail);
  }

}
