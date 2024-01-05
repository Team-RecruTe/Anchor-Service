package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.service.MentorInfoService;
import com.anchor.domain.mentor.api.service.MentorIntroductionService;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import com.anchor.global.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/mentors")
@Controller
public class MentorInfoViewController {

  private final MentorInfoService mentorInfoService;
  //private final MentorIntroductionService mentorIntroductionService;

  @GetMapping("/{id}") //방문자
  public String viewMentorPage(@PathVariable Long id, Model model){
    MentorInfoResponse result = mentorInfoService.findMentors(id);
    model.addAttribute("mentorInfo",result);
    return "멘토 정보 페이지 조회 성공";
  }

  @GetMapping("/me/info")
  public String viewMyInfoPage(HttpSession httpSession){
    //MentorInfoResponse mentorInfoResponse = mentorInfoService.
    //model.addAttribute();
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentorInfoResponse result = mentorInfoService.findMentor(user.getMentorId());
    return "멘토 필수정보 페이지 조회 성공";
  }

  @GetMapping("/me/introduction")
  public String viewMyIntroductionPage(HttpSession httpSession){ //Model model
    //MentorIntroductionResponse mentorIntroductionResponse = mentorIntroductionService.findMentors(id);
    //model.addAttribute();
    SessionUser user = (SessionUser) httpSession.getAttribute("user");
    MentorInfoResponse result = mentorInfoService.findMentor(user.getMentorId());
    return "멘토 소개글 페이지 조회 성공";
  }



}
