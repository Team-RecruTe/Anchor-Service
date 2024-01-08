package com.anchor.domain.user.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AppliedMentoringInfo {

  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String mentorNickname;
  private String mentoringTitle;
  private String impUid;

  @Builder
  private AppliedMentoringInfo(LocalDateTime startDateTime, LocalDateTime endDateTime,
      String mentorNickname, String mentoringTitle, String impUid) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.mentorNickname = mentorNickname;
    this.mentoringTitle = mentoringTitle;
    this.impUid = impUid;
  }

  public AppliedMentoringInfo(MentoringApplication mentoringApplication) {
    this.startDateTime = mentoringApplication.getStartDateTime();
    this.endDateTime = mentoringApplication.getEndDateTime();
    this.mentorNickname = mentoringApplication.getMentoring()
        .getMentor()
        .getUser()
        .getNickname();
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.impUid = mentoringApplication.getPayment()
        .getImpUid();
  }
}
