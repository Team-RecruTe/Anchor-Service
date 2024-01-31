package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 멘토링 결제페이지에 필요한 데이터입니다.
 */
@Getter
@NoArgsConstructor
public class MentoringPayConfirmInfo {

  private MentoringInfo mentoringInfo;
  private BuyerInfo buyerInfo;


  @Builder
  private MentoringPayConfirmInfo(MentoringInfo mentoringInfo, BuyerInfo buyerInfo) {
    this.mentoringInfo = mentoringInfo;
    this.buyerInfo = buyerInfo;
  }

  public static MentoringPayConfirmInfo of(User user, Mentoring mentoring, MentoringApplicationTime applicationTime) {
    return new MentoringPayConfirmInfo(MentoringInfo.of(mentoring, applicationTime), BuyerInfo.of(user));
  }

  @Getter
  @NoArgsConstructor
  static class MentoringInfo {

    private String mentoringTitle;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer amount;
    private Integer finalAmount;

    private MentoringInfo(String mentoringTitle, LocalDateTime startDateTime, LocalDateTime endDateTime,
        Integer amount) {
      this.mentoringTitle = mentoringTitle;
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.amount = amount;
      this.finalAmount = amount;
    }

    static MentoringInfo of(Mentoring mentoring, MentoringApplicationTime applicationTime) {
      return new MentoringInfo(mentoring.getTitle(), applicationTime.getFromDateTime(), applicationTime.getToDateTime(),
          mentoring.getCost());
    }

  }

  @Getter
  @NoArgsConstructor
  static class BuyerInfo {

    private String userNickname;
    private String userEmail;

    private BuyerInfo(String userNickname, String userEmail) {
      this.userNickname = userNickname;
      this.userEmail = userEmail;
    }

    static BuyerInfo of(User user) {
      return new BuyerInfo(user.getNickname(), user.getEmail());
    }

  }

}
