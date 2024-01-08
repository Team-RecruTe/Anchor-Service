package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorIntroductionResponse {

  private String contents;

  public MentorIntroductionResponse(MentorIntroduction mentorIntroduction){
    this.contents = mentorIntroduction.getContents();
  }

}