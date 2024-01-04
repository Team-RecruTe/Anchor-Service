package com.anchor.domain.payment.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.domain.Payment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public class PaidMentoringInfo implements Serializable {

  private String mentoringTitle;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime mentoringStartDateTime;

  private Integer amount;


  public PaidMentoringInfo(MentoringApplication mentoringApplication, Payment payment) {
    this.mentoringTitle = mentoringApplication.getMentoring()
        .getTitle();
    this.mentoringStartDateTime = mentoringApplication.getStartDateTime();
    this.amount = payment.getAmount();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PaidMentoringInfo that)) {
      return false;
    }
    return Objects.equals(getMentoringTitle(), that.getMentoringTitle()) && Objects.equals(
        getMentoringStartDateTime(), that.getMentoringStartDateTime()) && Objects.equals(getAmount(),
        that.getAmount());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getMentoringTitle(), getMentoringStartDateTime(), getAmount());
  }
}
