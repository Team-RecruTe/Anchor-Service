package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.MentoringViewService;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/mentorings")
@Controller
public class MentoringViewController {

  private final MentoringService mentoringService;
  private final MentoringViewService mentoringViewService;

  @GetMapping("/new")
  public String viewMentoringCreationPage() {
    return "mentoring-edit";
  }

  @GetMapping("/{id}/edit")
  public String viewMentoringEditPage(@PathVariable Long id, Model model) {
    MentoringContents result = mentoringService.getContents(id);
    model.addAttribute("mentoringContents", result);

    return "mentoring-edit";
  }

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
