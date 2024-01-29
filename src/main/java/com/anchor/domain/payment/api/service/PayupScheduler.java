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
   * 매달 1일 00:00:00 에 실행
   */
  @Scheduled(cron = "0 0 0 1 * *")
  public void payupProcess() {
    payupService.processMonthlyPayup();
  }

  /**
   * 테스트 용 스케줄러입니다. 어플리케이션 부팅완료 후 10초뒤 실행됩니다.
   */
//  @Scheduled(initialDelay = 10_000)
//  public void payupProcessTest() {
//    payupService.processMonthlyPayup();
//  }

}
