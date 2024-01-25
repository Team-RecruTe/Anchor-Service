package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.util.ResponseDto;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorContents extends ResponseDto {

  private String contents;

  public MentorContents(Mentor mentor) {
    this.contents = Objects.nonNull(mentor.getMentorIntroduction()) ? mentor.getMentorIntroduction()
        .getContents() : "";
  }

}