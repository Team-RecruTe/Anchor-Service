package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RequestMapping("/mentorings")
@Controller
public class MentoringViewController {

  private final ViewResolver viewResolver;
  private final MentoringService mentoringService;

  @GetMapping
  public String viewMentoringPage(
      @RequestParam(value = "tag", required = false) List<String> tags,
      @RequestParam(value = "keyword", required = false) String keyword,
      @PageableDefault(size = 16,
          sort = {"id", "totalApplicationNumber"}, direction = Sort.Direction.DESC) Pageable pageable,
      Model model
  ) {
    Page<MentoringSearchResult> result = mentoringService.getMentorings(tags, keyword, pageable);
    model.addAttribute("mentorings", result);
    return viewResolver.getViewPath("mentoring", "contents-edit");
  }

  @GetMapping("/new")
  public String viewMentoringCreationPage() {
    return viewResolver.getViewPath("mentoring", "contents-edit");
  }

  @GetMapping("/{id}/contents/edit")
  public String viewMentoringEditPage(@PathVariable Long id, Model model, HttpSession httpSession) {
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentoringContents result = mentoringService.getContents(id, 1L);
    model.addAttribute("mentoringContents", result);
    return viewResolver.getViewPath("mentoring", "contents-edit");
  }

}
