package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.ResponseDto;
import com.anchor.global.util.type.DateTimeRange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AppliedMentoringSearchResult extends ResponseDto {

  private MentoringInfo mentoringInfo;
  private UserInfo userInfo;
  private PaymentInfo paymentInfo;
  private MentoringApplicationInfo mentoringApplicationInfo;

  @Builder
  private AppliedMentoringSearchResult(
      Long totalPage,
      Long id,
      String title,
      String email,
      String nickname,
      String image,
      Integer amount,
      DateTimeRange range,
      String status
  ) {
    this.mentoringInfo = new MentoringInfo(id, title);
    this.userInfo = new UserInfo(email, nickname, image);
    this.paymentInfo = new PaymentInfo(amount);
    this.mentoringApplicationInfo = new MentoringApplicationInfo(range, status);
  }

  public static AppliedMentoringSearchResult of(MentoringApplication mentoringApplication) {
    Mentoring mentoring = mentoringApplication.getMentoring();
    User user = mentoringApplication.getUser();
    Payment payment = mentoringApplication.getPayment();

    return AppliedMentoringSearchResult.builder()
        .id(mentoring.getId())
        .title(mentoring.getTitle())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .image(user.getImage())
        .amount(payment.getAmount())
        .range(DateTimeRange.of(mentoringApplication.getStartDateTime(), mentoringApplication.getEndDateTime()))
        .status(mentoringApplication.getMentoringStatus()
            .getDescription())
        .build();
  }

  @Getter
  @AllArgsConstructor
  private static class MentoringInfo {

    private Long id;
    private String title;

  }

  @Getter
  @AllArgsConstructor
  private static class UserInfo {

    private String email;
    private String nickname;
    private String image;

  }

  @Getter
  @AllArgsConstructor
  private static class PaymentInfo {

    private Integer amount;

  }

  @Getter
  @AllArgsConstructor
  private static class MentoringApplicationInfo {

    private DateTimeRange dateTimeRange;
    private String status;

  }

}
