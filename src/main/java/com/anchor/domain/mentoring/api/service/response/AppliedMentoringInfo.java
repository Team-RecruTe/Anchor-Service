package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.payment.domain.Payment;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class AppliedMentoringInfo {

  private String mentorNickname;
  private String mentoringTitle;
  private LocalDateTime mentoringStartDateTime;
  private LocalDateTime mentoringEndDateTime;
  private MentoringStatus mentoringStatus;
  private String orderUid;
  private Integer amount;

  public AppliedMentoringInfo(MentoringApplication mentoringApplication, Payment payment) {
    this.mentorNickname = mentoringApplication.getMentoring()
        .getMentor()
        .getUser()
        .getNickname();
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.mentoringStartDateTime = mentoringApplication.getStartDateTime();
    this.mentoringEndDateTime = mentoringApplication.getEndDateTime();
    this.mentoringStatus = mentoringApplication.getMentoringStatus();
    this.amount = payment.getAmount();
    this.orderUid = payment.getOrderUid();
  }
}
