package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.user.domain.User;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.AnchorException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@SpringBootTest
public class ApplicationLockTest {

  @Autowired
  MentoringService mentoringService;

  MentoringApplicationTime mentoringApplicationTime = MentoringApplicationTime.of(LocalDate.of(2024, 2, 15),
      LocalTime.of(15, 0), "1h30m");
  String expectKey = "mentor:1:" + mentoringApplicationTime.convertDateTimeRange()
      .getFrom()
      .format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

  @AfterEach
  void tearDown() {
    mentoringService.unlock(expectKey);
  }

  @Test
  @DisplayName("결제진행중인 시간을 선택시 이미 잠금된 시간이 없다면 key를 반환받는다.")
  void applicationLockTest() {
    //given
    Long id = 1L;
    SessionUser sessionUser = new SessionUser(User.builder()
        .email("test@email.com")
        .build());

    //when
    String redisLockKey = mentoringService.lock(id, sessionUser, mentoringApplicationTime);

    //then
    assertThat(redisLockKey).isEqualTo(expectKey);
  }


  @Test
  @DisplayName("이미 잠금된 시간에 대해 요청이 들어온다면 예외를 발생시킨다.")
  void getApplicationLockFailTest() throws InterruptedException {
    //given
    Long id = 1L;
    SessionUser[] array = {
        new SessionUser(User.builder()
            .email("test1@email.com")
            .build()),
        new SessionUser(User.builder()
            .email("test2@email.com")
            .build()),
        new SessionUser(User.builder()
            .email("test3@email.com")
            .build()),
        new SessionUser(User.builder()
            .email("test4@email.com")
            .build()),
        new SessionUser(User.builder()
            .email("test5@email.com")
            .build())
    };
    AtomicInteger failCount = new AtomicInteger();
    int threadCount = 5;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch endLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      int index = i;
      executorService.submit(() -> {
        try {
          mentoringService.lock(id, array[index], mentoringApplicationTime);
        } catch (AnchorException e) {
          log.info("실패");
          failCount.getAndIncrement();
        } finally {
          endLatch.countDown();
        }
      });
    }
    endLatch.await();

    assertThat(failCount.get()).isEqualTo(4);

  }
}
