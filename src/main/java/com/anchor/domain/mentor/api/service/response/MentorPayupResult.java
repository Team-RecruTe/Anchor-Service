package com.anchor.domain.mentor.api.service.response;

import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorPayupResult {

  private Map<LocalDateTime, Integer> dailyTotalAmount;

  private Map<LocalDateTime, List<PayupInfo>> dailyMentoringPayupInfo;

  public MentorPayupResult(Map<LocalDateTime, Integer> dailyTotalAmount,
      Map<LocalDateTime, List<PayupInfo>> dailyMentoringPayupInfo) {
    this.dailyTotalAmount = dailyTotalAmount;
    this.dailyMentoringPayupInfo = dailyMentoringPayupInfo;
  }

  @Getter
  @NoArgsConstructor
  public static class PayupInfo {

    private DateTimeRange dateTimeRange;
    private String menteeNickname;
    private Integer payupAmount;

    public PayupInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, String nickname, Integer amount) {
      this.dateTimeRange = DateTimeRange.of(startDateTime, endDateTime);
      this.menteeNickname = nickname;
      this.payupAmount = amount;
    }
  }

}
