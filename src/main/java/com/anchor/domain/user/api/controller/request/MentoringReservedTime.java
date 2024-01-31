package com.anchor.domain.user.api.controller.request;

import com.anchor.global.util.type.DateTimeRange;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Getter;

@Getter
public class MentoringReservedTime {


  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @ConstructorProperties({"startTime", "endTime"})
  private MentoringReservedTime(String startTime, String endTime) {
    this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    this.endTime = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }

  public static MentoringReservedTime of(DateTimeRange timeRange) {
    return new MentoringReservedTime(timeRange.getFrom()
        .toString(), timeRange.getTo()
        .toString());
  }
}
