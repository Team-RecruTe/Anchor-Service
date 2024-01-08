package com.anchor.domain.mentor.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.anchor.domain.mentor.api.controller.request.MentoringUnavailableTimeInfos.DateTimeRange;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.global.aws.AwsS3Config;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@WithMockUser(username = "hossi", roles = {"USER, MENTOR"})
@EnableJpaRepositories(basePackages = "com.anchor")
@ComponentScan(basePackages = "com.anchor")
@ContextConfiguration(classes = {AwsS3Config.class})
@ExtendWith(value = SpringExtension.class)
@AutoConfigureDataJpa
@SpringBootTest
class MentorServiceTest {

  @Autowired
  MentorRepository mentorRepository;

  @Autowired
  MentorService mentorService;

  @Transactional
  @DisplayName("멘토 아이디와 불가능한 시간대를 입력받아, 멘토링 불가능한 시간대를 저장하고, 아무것도 반환하지 않습니다.")
  @Test
  void setUnavailableTimes() {
    // given
    List<DateTimeRange> dateTimeRanges = List.of(
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 20, 30, 0),
            LocalDateTime.of(2023, 12, 12, 21, 30, 0)
        ),
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 21, 30, 0),
            LocalDateTime.of(2023, 12, 12, 22, 30, 0)
        ),
        DateTimeRange.of(
            LocalDateTime.of(2023, 12, 12, 22, 30, 0),
            LocalDateTime.of(2023, 12, 12, 23, 30, 0)
        )
    );

    // when
    mentorService.setUnavailableTimes(1L, dateTimeRanges);
    Mentor mentor = mentorRepository.findById(1L)
        .get();
    List<MentoringUnavailableTime> mentoringUnavailableTimes = mentor.getMentoringUnavailableTimes();

    // then
    assertThat(mentoringUnavailableTimes)
        .extracting("fromDateTime", "toDateTime")
        .contains(
            tuple(
                LocalDateTime.of(2023, 12, 12, 20, 30, 0),
                LocalDateTime.of(2023, 12, 12, 21, 30, 0)
            ),
            tuple(
                LocalDateTime.of(2023, 12, 12, 21, 30, 0),
                LocalDateTime.of(2023, 12, 12, 22, 30, 0)
            ),
            tuple(
                LocalDateTime.of(2023, 12, 12, 22, 30, 0),
                LocalDateTime.of(2023, 12, 12, 23, 30, 0)
            )
        );
  }
}