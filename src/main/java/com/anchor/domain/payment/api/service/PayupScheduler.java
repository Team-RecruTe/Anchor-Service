package com.anchor.domain.payment.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayupScheduler {

  private final PayupService payupService;

  /**
   * 매달 1일 03:00:00 에 실행
   */
  @Scheduled(cron = "0 0 3 1 * *")
  public void payupProcess() {
    payupService.processMonthlyPayup();
  }

}
