package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringReview;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mentorings")
@Controller
public class MentoringViewController {

  private final MentoringService mentoringService;
  private final boolean aBoolean = false;

  @GetMapping
  public String viewMentoringPage(
      @RequestParam(value = "tag", required = aBoolean) List<String> tags,
      @RequestParam(value = "keyword", required = false) String keyword,
      @PageableDefault(size = 16,
          sort = {"id", "totalApplicationNumber"}, direction = Sort.Direction.DESC) Pageable pageable,
      Model model
  ) {
    Page<MentoringSearchResult> result = mentoringService.getMentorings(tags, keyword, pageable);
    model.addAttribute("mentorings", result);

    return "mentoring-edit";
  }


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

  @GetMapping("/{id}/reviews")
  public String viewReviewPage(@PathVariable("id") Long mentoringId, Model model) {
    List<MentoringReview> reviewList = mentoringService.getMentoringReviews(mentoringId);
    model.addAttribute("reviewList", reviewList);
    log.info("reviewList===" + reviewList);
    return "/mentoring-review";
  }
}
