package com.anchor.domain.user.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.user.api.controller.request.MentoringReservedTime;
import com.anchor.domain.user.api.controller.request.RequiredEditReview;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserViewController {

  private final UserService userService;
  private final ViewResolver viewResolver;

  @GetMapping("/me")
  public String getInfo(Model model, HttpSession session) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    UserInfoResponse userInfoResponse = userService.getProfile(sessionUser);
    model.addAttribute("userInfo", userInfoResponse);
    return viewResolver.getViewPath("user", "userInfo");
  }

  /**
   * 멘토링 신청내역을 조회합니다.
   */
  @GetMapping("/me/applied-mentorings")
  public String appliedMentoringList(@PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable,
      HttpSession session, Model model) {

    SessionUser sessionUser = SessionUser.getSessionUser(session);

    Page<AppliedMentoringInfo> appliedMentoringInfoList = userService.loadAppliedMentoringList(
        sessionUser, pageable);

    model.addAttribute("mentoringApplications", appliedMentoringInfoList);
    return viewResolver.getViewPath("user", "user-mentoring-application");
  }

  @GetMapping("/me/applied-mentorings/review")
  public String review(@ModelAttribute MentoringReservedTime reservedTime, Model model) {
    model.addAttribute("mentoringReviewInfo", MentoringReviewInfo.builder()
        .build());
    model.addAttribute("mentoringReservedTime", reservedTime);
    return viewResolver.getViewPath("user", "review-write");
  }

  @PostMapping("/me/applied-mentorings/review")
  public String reviewProcess(@Valid @ModelAttribute MentoringReviewInfo mentoringReviewInfo,
      BindingResult bindingResult, HttpSession session, Model model) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    if (bindingResult.hasErrors()) {
      model.addAttribute("mentoringReservedTime", MentoringReservedTime.of(mentoringReviewInfo.getTimeRange()));
      model.addAttribute("mentoringReviewInfo", mentoringReviewInfo);
      return viewResolver.getViewPath("user", "review-write");
    }
    userService.writeReview(sessionUser, mentoringReviewInfo);
    return "redirect:/users/me/applied-mentorings";
  }

  @GetMapping("/me/review/edit")
  public String viewReviewEditPage(@ModelAttribute MentoringReservedTime reservedTime, HttpSession session,
      Model model) {
    SessionUser sessionUser = SessionUser.getSessionUser(session);
    RequiredEditReview review = userService.getReview(sessionUser, reservedTime);
    model.addAttribute("requiredEditReview", review);
    return viewResolver.getViewPath("user", "edit-review");
  }

  @PostMapping("/me/review/edit")
  public String editReview(@Validated @ModelAttribute RequiredEditReview requiredEditReview,
      BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("requiredEditReview", requiredEditReview);
      return viewResolver.getViewPath("user", "edit-review");
    }
    userService.editReview(requiredEditReview);
    return "redirect:/users/me/applied-mentorings";
  }
}