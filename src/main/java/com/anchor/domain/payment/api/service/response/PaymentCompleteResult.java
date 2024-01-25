package com.anchor.domain.payment.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.global.util.type.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PaymentCompleteResult {

  private MentoringInfo mentoringInfo;
  private PaymentInfo paymentInfo;

  private PaymentCompleteResult(MentoringInfo mentoringInfo, PaymentInfo paymentInfo) {
    this.mentoringInfo = mentoringInfo;
    this.paymentInfo = paymentInfo;
  }

  public static PaymentCompleteResult of(MentoringApplication mentoringApplication) {
    Mentoring mentoring = mentoringApplication.getMentoring();
    String mentorNickname = mentoring.getMentor()
        .getUser()
        .getNickname();
    DateTimeRange mentoringReservedTime = DateTimeRange.of(mentoringApplication.getStartDateTime(),
        mentoringApplication.getEndDateTime());
    Payment payment = mentoringApplication.getPayment();
    return new PaymentCompleteResult(
        new MentoringInfo(mentorNickname, mentoring.getTitle(), mentoringReservedTime),
        new PaymentInfo(payment.getOrderUid(), payment.getAmount())
    );
  }

  @Getter
  @AllArgsConstructor
  static class MentoringInfo {

    private String mentorNickname;
    private String mentoringTitle;
    private DateTimeRange mentoringReservedTime;

  }

  @Getter
  @AllArgsConstructor
  static class PaymentInfo {

    private String orderUid;
    private Integer amount;

  }
}
