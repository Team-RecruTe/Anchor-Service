package com.anchor.domain.mentor.api.service.response;

import com.anchor.global.util.ResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorContents extends ResponseDto {

  private String contents;

  public MentorContents(String contents){
    this.contents = contents;
  }

}