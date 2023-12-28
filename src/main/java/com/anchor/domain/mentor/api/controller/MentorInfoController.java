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

  @GetMapping("/{id}")
  public String getMentors(@PathVariable Long id){ //Model model
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findMentors(id);
    //model.addAttribute();
    return "멘토 정보 페이지 조회 성공";
  }


  @GetMapping("/{id}/info")
  public String getMentorsInfo(@PathVariable Long id ){ //@AuthenticationPrincipal , Model model
    MentorInfoResponse mentorInfoResponse = mentorInfoService.findMentors(id);
    //model.addAttribute();
    return "멘토 필수정보 페이지 조회 성공";
  }


  @PutMapping("/{id}/info")
  public Map<String, Object> putMentorsInfo(@PathVariable Long id,  //@AuthenticationPrincipal , Model model
                                              @RequestBody MentorInfoRequest mentorInfoRequest){
    //model.addAttribute();
    mentorInfoService.modifyMentorsInfo(id, mentorInfoRequest);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("modify", "ok");
    return resultMap;
  }


  @DeleteMapping("/{id}")
  public Map<String, Object> deleteMentors(@PathVariable Long id){
    mentorInfoService.deleteMentors(id);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("delete", "ok");
    return resultMap;

  }



}
