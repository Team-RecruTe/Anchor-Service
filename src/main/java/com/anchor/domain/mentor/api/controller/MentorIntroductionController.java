package com.anchor.domain.mentor.api.controller;

import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentor.api.service.MentorIntroductionService;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController("/mentors")
public class MentorIntroductionController {

  private final MentorIntroductionService mentorIntroductionService;

  @GetMapping("/{id}/introduction")
  public String getMentorsIntroduction(@PathVariable Long id){ //Model model
    MentorIntroductionResponse mentorIntroductionResponse = mentorIntroductionService.findMentors(id);
    //model.addAttribute();
    return "멘토 소개글 페이지 조회 성공";
  }

  @PutMapping("/{id}/introduction")
  public Map<String, Object> putMentorsIntroduction(@PathVariable Long id,
      @RequestBody MentorIntroductionRequest mentorIntroductionRequest){
    mentorIntroductionService.editContents(id, mentorIntroductionRequest);
    Map<String, Object> resultMap = new HashMap<>();
    resultMap.put("modify","ok");
    return resultMap;
  }


}