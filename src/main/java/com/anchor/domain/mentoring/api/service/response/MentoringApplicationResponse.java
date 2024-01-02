package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MentoringApplicationResponse {

  private String mentorNickname;
  private String mentoringTitle;
  private LocalDateTime mentoringStartDateTime;
  private LocalDateTime mentoringEndDateTime;
  private MentoringStatus mentoringStatus;

  public MentoringApplicationResponse(MentoringApplication mentoringApplication) {
    this.mentorNickname = mentoringApplication.getMentoring()
        .getMentor()
        .getUser()
        .getNickname();
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.mentoringStartDateTime = mentoringApplication.getStartDateTime();
    this.mentoringEndDateTime = mentoringApplication.getEndDateTime();
    this.mentoringStatus = mentoringApplication.getMentoringStatus();
  }
}
