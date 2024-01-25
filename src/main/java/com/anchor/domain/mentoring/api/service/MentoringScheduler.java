package com.anchor.domain.mentoring.api.service;

import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentoringScheduler {

  private final MentoringService mentoringService;

  @Scheduled(cron = "0 0 2 * * *")
  public void autoChangeStatusToComplete() {
    LocalDateTime baseDate = LocalDateTime.now()
        .minusDays(1L)
        .truncatedTo(ChronoUnit.DAYS);
    LocalDateTime targetDateStart = baseDate.minusWeeks(1L);
    LocalDateTime targetDateEnd = targetDateStart.plusDays(1L);
    DateTimeRange targetDateRange = DateTimeRange.of(targetDateStart, targetDateEnd);
    mentoringService.autoChangeStatus(targetDateRange);
  }

  @Scheduled(initialDelay = 5_000)
  public void autoChange() {
    LocalDateTime baseDate = LocalDateTime.now()
        .minusDays(1L)
        .truncatedTo(ChronoUnit.DAYS);
    LocalDateTime targetDateStart = baseDate.minusWeeks(1L);
    LocalDateTime targetDateEnd = targetDateStart.plusDays(1L);
    DateTimeRange targetDateRange = DateTimeRange.of(targetDateStart, targetDateEnd);
    mentoringService.autoChangeStatus(targetDateRange);
  }
}
