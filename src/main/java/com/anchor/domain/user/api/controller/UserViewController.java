package com.anchor.domain.user.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.user.api.controller.request.MentoringReservedTime;
import com.anchor.domain.user.api.controller.request.RequiredEditReview;
import com.anchor.domain.user.api.service.UserService;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@RequiredArgsConstructor
@RequestMapping("/users")
@Controller
public class UserViewController {

  private final UserService userService;
  private final ViewResolver viewResolver;

  /**
   * 유저 정보 페이지를 조회합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @GetMapping("/me")
  public String viewUserInfoPage(Model model, @SessionAttribute("user") SessionUser user) {
    UserInfoResponse userInfoResponse = userService.getProfile(user);
    model.addAttribute("userInfo", userInfoResponse);
    return viewResolver.getViewPath("user", "userInfo");
  }

  /**
   * 멘토링 신청내역 페이지를 조회합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @GetMapping("/me/applied-mentorings")
  public String viewAppliedMentoringListPage(
      @PageableDefault(sort = {"id"}, direction = Direction.DESC) Pageable pageable,
      @SessionAttribute("user") SessionUser user, Model model) {
    Page<AppliedMentoringInfo> appliedMentoringInfoList = userService.loadAppliedMentoringList(user, pageable);
    model.addAttribute("mentoringApplications", appliedMentoringInfoList);
    return viewResolver.getViewPath("user", "user-mentoring-application");
  }

  /**
   * 멘토링 리뷰 작성 페이지를 조회합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @GetMapping("/me/applied-mentorings/review")
  public String viewReviewEditPage(@ModelAttribute MentoringReservedTime reservedTime, Model model) {
    model.addAttribute("mentoringReviewInfo", MentoringReviewInfo.builder()
        .build());
    model.addAttribute("mentoringReservedTime", reservedTime);
    return viewResolver.getViewPath("user", "review-write");
  }

  /**
   * 멘토링 리뷰 작성을 처리하고, 멘토링 신청내역 페이지로 리다이렉트합니다.
   * 입력 값이 잘못되면 재작성하도록 합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @PostMapping("/me/applied-mentorings/review")
  public String processReview(@Valid @ModelAttribute MentoringReviewInfo mentoringReviewInfo,
      BindingResult bindingResult, @SessionAttribute("user") SessionUser user, Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("mentoringReservedTime", MentoringReservedTime.of(mentoringReviewInfo.getTimeRange()));
      model.addAttribute("mentoringReviewInfo", mentoringReviewInfo);
      return viewResolver.getViewPath("user", "review-write");
    }
    userService.writeReview(user, mentoringReviewInfo);
    return "redirect:/users/me/applied-mentorings";
  }

  /**
   * 멘토링 리뷰 수정 페이지를 조회합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
  @GetMapping("/me/review/edit")
  public String viewReviewEditPage(@ModelAttribute MentoringReservedTime reservedTime,
      @SessionAttribute("user") SessionUser user, Model model) {
    RequiredEditReview review = userService.getReview(user, reservedTime);
    model.addAttribute("requiredEditReview", review);
    return viewResolver.getViewPath("user", "edit-review");
  }

  /**
   * 멘토링 리뷰 수정을 처리하고, 멘토링 신청내역 페이지로 리다이렉트합니다.
   * 입력 값이 잘못되면 재작성하도록 합니다.
   */
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MENTOR')")
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