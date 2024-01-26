package com.anchor.global.redis.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RedisLockTest {

  Mentoring savedMentoring;
  Mentor savedMentor;

  @Autowired
  private RedisLockFacade redisLockFacade;
  @Autowired
  private MentoringRepository mentoringRepository;
  @Autowired
  private MentorRepository mentorRepository;

  @BeforeEach
  void setStock() {
    Mentor mentor = Mentor.builder()
        .accountName("홍길동")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("siaadssdj@naver.com")
        .bankName("한국은행")
        .build();
    savedMentor = mentorRepository.save(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title("멘토링")
        .durationTime("1h30m")
        .cost(10_000)
        .mentor(mentor)
        .mentoringDetail(null)
        .build();

    savedMentoring = mentoringRepository.saveAndFlush(mentoring);
  }

  @AfterEach
  void tearDown() {
    mentorRepository.deleteById(savedMentor.getId());
    mentoringRepository.deleteById(savedMentoring.getId());
  }

  @Test
  @DisplayName("멘토링 신청자 수에 대한 동시성을 제어합니다.")
  void increaseTotalApplicationNumber() throws InterruptedException {
    int threadCount = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executorService.submit(() -> {
        try {
          redisLockFacade.increaseTotalApplication(savedMentoring.getId());
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          countDownLatch.countDown();
        }
      });
    }
    countDownLatch.await();

    Mentoring finalMentoring = mentoringRepository.findById(savedMentoring.getId())
        .get();

    assertEquals(100, finalMentoring.getTotalApplicationNumber());
  }
}