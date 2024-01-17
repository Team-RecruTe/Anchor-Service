package com.anchor.domain.mentoring.api.controller;

import com.anchor.domain.mentoring.api.service.MentoringService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class MentoringCacheTest {

  private static Logger logger = LoggerFactory.getLogger(MentoringCacheTest.class);
  @Autowired
  private MentoringService mentoringService;

  @Test
  @DisplayName("캐싱 이전과 이후의 속도를 비교합니다.")
  void test() {
    long start = System.currentTimeMillis();
    mentoringService.getTopMentorings();
    long end = System.currentTimeMillis();
    long notCached = end - start;

    start = System.currentTimeMillis();
    mentoringService.getTopMentorings();
    end = System.currentTimeMillis();
    long cached = end - start;

    logger.info("캐싱 처리 전 : {}ms", notCached);
    logger.info("캐싱 처리 후 : {}ms", cached);

    Assertions.assertThat(notCached)
        .isGreaterThan(cached);
  }

}
