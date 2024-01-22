package com.anchor.domain.user.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.type.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AppliedMentoringInfo {

  private MentoringInfo mentoringInfo;
  private MentorInfo mentorInfo;
  private PaymentInfo paymentInfo;
  private MentoringApplicationInfo mentoringApplicationInfo;

  @Builder
  private AppliedMentoringInfo(Long id, String title, String email, String nickname, String image, Integer amount,
      String orderUid, DateTimeRange dateTimeRange, String status) {
    this.mentoringInfo = new MentoringInfo(id, title);
    this.mentorInfo = new MentorInfo(email, nickname, image);
    this.paymentInfo = new PaymentInfo(orderUid, amount);
    this.mentoringApplicationInfo = new MentoringApplicationInfo(dateTimeRange, status);
  }

  public static AppliedMentoringInfo of(MentoringApplication application) {
    Mentoring mentoring = application.getMentoring();
    User mentor = mentoring.getMentor()
        .getUser();
    Payment payment = application.getPayment();

    return AppliedMentoringInfo.builder()
        .id(mentoring.getId())
        .title(mentoring.getTitle())
        .email(mentor.getEmail())
        .nickname(mentor.getNickname())
        .image(mentor.getImage())
        .amount(payment.getAmount())
        .orderUid(payment.getOrderUid())
        .dateTimeRange(DateTimeRange.of(application.getStartDateTime(), application.getEndDateTime()))
        .status(application.getMentoringStatus()
            .getDescription())
        .build();
  }

  @Getter
  @AllArgsConstructor
  static class MentoringInfo {

    private Long id;
    private String title;
  }

  @Getter
  @AllArgsConstructor
  static class MentorInfo {

    private String email;
    private String nickname;
    private String image;

  }

  @Getter
  @AllArgsConstructor
  static class PaymentInfo {

    private String orderUid;
    private Integer amount;
  }

  @Getter
  @AllArgsConstructor
  static class MentoringApplicationInfo {

    private DateTimeRange dateTimeRange;
    private String status;
  }

}
