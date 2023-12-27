package com.anchor.domain.mentoring.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/mentorings")
@Controller
public class MentoringViewController {

  @GetMapping("/new")
  public String viewMentoringCreationPage() {
    return "멘토링 생성 페이지";
  }

  @GetMapping("/{id}/edit")
  public String viewMentoringEditPage() {
    return "멘토링 수정 페이지";
  }

}
