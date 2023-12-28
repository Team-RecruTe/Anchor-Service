package com.anchor.domain.user.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AppliedMentoringInfo {

  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private String mentorNickName;
  private String mentoringTitle;
  private Long paymentId;

  @Builder
  private AppliedMentoringInfo(LocalDateTime startDateTime, LocalDateTime endDateTime,
      String mentorNickName, String mentoringTitle, Long paymentId) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.mentorNickName = mentorNickName;
    this.mentoringTitle = mentoringTitle;
    this.paymentId = paymentId;
  }

  public AppliedMentoringInfo(MentoringApplication mentoringApplication) {
    this.startDateTime = mentoringApplication.getStartDateTime();
    this.endDateTime = mentoringApplication.getEndDateTime();
    this.mentorNickName = mentoringApplication.getMentoring()
        .getMentor()
        .getUser()
        .getNickname();
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.paymentId = mentoringApplication.getPayment()
        .getId();
  }
}
