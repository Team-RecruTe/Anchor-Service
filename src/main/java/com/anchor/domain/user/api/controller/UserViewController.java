package com.anchor.domain.user.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.user.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController //페이지 생성 후 컨트롤러로 변경할 것
public class UserViewController {

  private final UserService userService;

  @GetMapping("/me/applied-mentorings/{id}/review")
  public String review(@PathVariable Long id) {
    return "리뷰 작성 페이지로 이동";
  }

  @PostMapping("/me/applied-mentorings/{id}/review")
  public String reviewProcess(@PathVariable Long id, @ModelAttribute MentoringReviewInfo mentoringReviewInfo) {
    userService.writeReview(id, mentoringReviewInfo);
    return "리뷰 작성 성공";
  }

}
