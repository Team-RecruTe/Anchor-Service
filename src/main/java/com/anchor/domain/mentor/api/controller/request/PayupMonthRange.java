package com.anchor.domain.mentor.api.controller.request;

import com.anchor.global.valid.NotFutureMonth;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class PayupMonthRange {

  @NotFutureMonth(message = "이번달 보다 미래시점의 조회는 불가능합니다.")
  private LocalDateTime currentMonth;

  private LocalDateTime startMonth;

  @Builder
  @ConstructorProperties({"currentMonth", "startMonth"})
  private PayupMonthRange(
      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime currentMonth,
      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime startMonth
  ) {
    this.currentMonth = currentMonth;
    this.startMonth = startMonth;
  }

}
