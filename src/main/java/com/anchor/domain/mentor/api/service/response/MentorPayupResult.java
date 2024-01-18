package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.payment.domain.PayupStatus;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorPayupResult {

  private Map<LocalDateTime, PayupTotalAmount> dailyTotalAmount;
  private Map<LocalDateTime, List<PayupInfo>> dailyMentoringPayupInfo;

  private MentorPayupResult(Map<LocalDateTime, PayupTotalAmount> dailyTotalAmount,
      Map<LocalDateTime, List<PayupInfo>> dailyMentoringPayupInfo) {
    this.dailyTotalAmount = dailyTotalAmount;
    this.dailyMentoringPayupInfo = dailyMentoringPayupInfo;
  }

  public static MentorPayupResult of(List<PayupInfo> payupInfos) {
    Map<LocalDateTime, PayupTotalAmount> dailyTotalAmount = new HashMap<>();
    Map<LocalDateTime, List<PayupInfo>> dailyMentoringPayupInfo = new HashMap<>();
    payupInfos.forEach(info -> {
      LocalDateTime startDateTime = info.getDateTimeRange()
          .getFrom()
          .truncatedTo(ChronoUnit.DAYS);
      dailyMentoringPayupInfo.computeIfAbsent(startDateTime, key -> new ArrayList<>())
          .add(info);
      dailyTotalAmount.computeIfAbsent(startDateTime, key -> new PayupTotalAmount())
          .sum(info.getPayupStatus(), info.getPayupAmount());
    });
    return new MentorPayupResult(dailyTotalAmount, dailyMentoringPayupInfo);
  }

  @Getter
  @NoArgsConstructor
  public static class PayupInfo {

    private DateTimeRange dateTimeRange;
    private String menteeNickname;
    private Integer payupAmount;
    private PayupStatus payupStatus;

    public PayupInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, String nickname, Integer amount,
        PayupStatus payupStatus) {
      this.dateTimeRange = DateTimeRange.of(startDateTime, endDateTime);
      this.menteeNickname = nickname;
      this.payupAmount = amount;
      this.payupStatus = payupStatus;
    }
  }

  @Getter
  static class PayupTotalAmount {

    private final Map<PayupStatus, Integer> totalAmount = new EnumMap<>(PayupStatus.class);

    public void sum(PayupStatus status, Integer amount) {
      totalAmount.merge(status, amount, Integer::sum);
    }

  }
}
