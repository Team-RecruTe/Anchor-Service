package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo.MentoringDetailSearchResult;
import com.anchor.domain.mentoring.api.service.response.MentoringPayConfirmInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.view.ViewResolver;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/mentorings")
@Controller
public class MentoringViewController {

  private final ViewResolver viewResolver;
  private final MentoringService mentoringService;

  /**
   * 검색어와 태그를 사용해 멘토링 목록을 조회합니다. 검색어 또는 태그 미입력시 전체 목록을 반환합니다.
   */
  @GetMapping
  public String viewMentoringPage(
      @RequestParam(value = "tag", required = false) List<String> tags,
      @RequestParam(value = "keyword", required = false) String keyword,
      @PageableDefault(size = 16,
          sort = "totalApplicationNumber", direction = Sort.Direction.DESC) Pageable pageable,
      Model model
  ) {
    Page<MentoringSearchResult> mentoringSearchResults = mentoringService.getMentorings(tags, keyword, pageable);
    Set<String> popularMentoringTags = mentoringService.getPopularMentoringTags();
    MentoringSearchInfo mentoringSearchInfo = MentoringSearchInfo.of(mentoringSearchResults, popularMentoringTags);
    model.addAttribute("mentoringSearchInfo", mentoringSearchInfo);
    return viewResolver.getViewPath("mentoring", "mentoring-search");
  }

  /**
   * 멘토링 상세내용 페이지를 조회합니다.
   */
  @GetMapping("/{id}")
  public String viewMentoringDetailPage(@PathVariable("id") Long id, Model model) {
    MentoringDetailSearchResult mentoringDetailSearchResult = mentoringService.getMentoringDetailInfo(id);
    Set<String> popularMentoringTags = mentoringService.getPopularMentoringTags();
    MentoringDetailInfo mentoringDetailInfo = MentoringDetailInfo.of(mentoringDetailSearchResult, popularMentoringTags);
    model.addAttribute("mentoringDetail", mentoringDetailInfo);
    return viewResolver.getViewPath("mentoring", "mentoring-detail");
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

  /**
   * 멘토링 결제페이지로 이동합니다. 결제할 멘토링정보, 신청한 시간이 데이터로 반환됩니다.
   */
  @PostMapping("/{id}/payment")
  public String viewMentoringPayment(@PathVariable("id") Long id,
      @ModelAttribute MentoringApplicationTime applicationTime, HttpSession session, Model model) {
    SessionUser sessionUser = getSessionUser(session);
    MentoringPayConfirmInfo mentoringConfirmInfo = mentoringService.getMentoringConfirmInfo(id, applicationTime,
        sessionUser);
    model.addAttribute("confirmInfo", mentoringConfirmInfo);
    return viewResolver.getViewPath("mentoring", "mentoring-payment");
  }

  @GetMapping("/{id}/reviews")
  public String viewReviewPage(@PathVariable("id") Long mentoringId, Model model) {
    List<MentoringReview> reviewList = mentoringService.getMentoringReviews(mentoringId);
    model.addAttribute("reviewList", reviewList);
    log.info("reviewList===" + reviewList);
    return "/mentoring-review";
  }

  private SessionUser getSessionUser(HttpSession session) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("user");
    if (sessionUser == null) {
      throw new RuntimeException("로그인 정보가 없습니다. 잘못된 접근입니다.");
    }
    return sessionUser;
  }

}
