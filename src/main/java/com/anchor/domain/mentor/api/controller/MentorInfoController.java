package com.anchor.domain.mentor.api.controller;


import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.MentorInfoService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/mentors")
@RestController
public class MentorInfoController {

  private final MentorInfoService mentorInfoService;

  @GetMapping("/{id}") //방문자
  public String getVisitorView(@PathVariable Long id){ //Model model
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findMentors(id);
    //멘토 소개글, 멘토링 내용...
    //model.addAttribute();
    return "멘토 정보 페이지 조회 성공";
  }

  @GetMapping("/{id}/info") //멘토회원
  public String getMentorView(@PathVariable Long id){ //@AuthenticationPrincipal , Model model
    //MentorInfoResponse mentorInfoResponse = mentorInfoService.
    //model.addAttribute();
    return "멘토 필수정보 페이지 조회 성공";
  }

  @PutMapping("/{id}/info") //멘토회원
  public Map<String, Object> putInfo(@PathVariable Long id,  //@AuthenticationPrincipal , Model model
                                              @RequestBody MentorInfoRequest mentorInfoRequest){
    //model.addAttribute();
    mentorInfoService.editMentorsInfo(id, mentorInfoRequest);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("modify", "ok");
    return resultMap;
  }

  @DeleteMapping("/{id}") //멘토회원
  public Map<String, Object> deleteMentors(@PathVariable Long id){
    mentorInfoService.deleteMentors(id);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("delete", "ok");
    return resultMap;
  }

}
