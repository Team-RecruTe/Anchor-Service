package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import lombok.RequiredArgsConstructor;
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

}
