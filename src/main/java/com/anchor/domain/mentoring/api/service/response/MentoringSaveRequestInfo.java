package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MentoringSaveRequestInfo {

  private String mentorNickname;
  private String mentoringTitle;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime mentoringStartDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime mentoringEndDateTime;

  private MentoringStatus mentoringStatus;
  private String orderUid;
  private Integer amount;

  public MentoringSaveRequestInfo(MentoringApplication mentoringApplication) {
    this.mentorNickname = mentoringApplication.getMentoring()
        .getMentor()
        .getUser()
        .getNickname();
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.mentoringStartDateTime = mentoringApplication.getStartDateTime();
    this.mentoringEndDateTime = mentoringApplication.getEndDateTime();
    this.mentoringStatus = mentoringApplication.getMentoringStatus();
    this.amount = mentoringApplication.getPayment()
        .getAmount();
    this.orderUid = mentoringApplication.getPayment()
        .getOrderUid();
  }
}
