package com.anchor.domain.mentor.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.config.QueryDslConfig;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("멘토링 서비스 테스트 - DB 의존성 포함")
@ActiveProfiles("test")
@Import({QueryDslConfig.class, MentorService.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
class MentorDataTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  MentorRepository mentorRepository;

  @Autowired
  MentoringRepository mentoringRepository;

  @Autowired
  MentoringApplicationRepository mentoringApplicationRepository;

  @Autowired
  MentorService mentorService;

  @Transactional
  @DisplayName("멘토 아이디와 불가능한 시간대를 입력받아 멘토링 불가능한 시간대를 저장합니다.")
  @Test
  void setUnavailableTimes() {
    // given
    Mentor mentor = Mentor.builder()
        .accountName("홍길동")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("random@naver.com")
        .bankName("한국은행")
        .build();
    Mentor savedMentor = mentorRepository.save(mentor);

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
    mentorService.setUnavailableTimes(savedMentor.getId(), dateTimeRanges);
    List<MentoringUnavailableTime> mentoringUnavailableTimes = mentorRepository.findUnavailableTimes(
        savedMentor.getId());

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

  @Transactional
  @DisplayName("멘토 아이디와 신청된 멘토링 진행날짜 및 상태 정보들을 이용해 멘토링 상태를 취소합니다.")
  @Test
  void cancelMentoringStatus() {
    //given
    Mentor mentor = Mentor.builder()
        .accountName("홍길동")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("random@naver.com")
        .bankName("한국은행")
        .build();
    Mentor savedMentor = mentorRepository.save(mentor);

    Mentoring mentoring = Mentoring.createMentoring(mentor, MentoringBasicInfo.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .build());
    mentoringRepository.save(mentoring);

    User user = User.builder()
        .email("asdf@naver.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("홍홍홍")
        .build();
    User savedUser = userRepository.save(user);

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .startDateTime(
            LocalDateTime.of(2023, 12, 12, 20, 30, 0))
        .endDateTime(
            LocalDateTime.of(2023, 12, 12, 21, 30, 0))
        .user(savedUser)
        .mentoring(mentoring)
        .payment(null)
        .mentoringStatus(MentoringStatus.WAITING)
        .build();
    MentoringApplication savedMentoringApplication = mentoringApplicationRepository.save(mentoringApplication);

    List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos = new ArrayList<>();
    RequiredMentoringStatusInfo requiredMentoringStatusInfo = RequiredMentoringStatusInfo.builder()
        .mentoringReservedTime(
            DateTimeRange.of(
                LocalDateTime.of(
                    2023, 12, 12, 20,
                    30, 0),
                LocalDateTime.of(
                    2023, 12, 12, 21,
                    30, 0)
            ))
        .mentoringStatus(
            MentoringStatus.CANCELLED.name())
        .build();
    requiredMentoringStatusInfos.add(requiredMentoringStatusInfo);

    // when
    mentorService.changeMentoringStatus(savedMentor.getId(), requiredMentoringStatusInfos);
    MentoringApplication updatedMentoringApplication =
        mentoringApplicationRepository.findById(savedMentoringApplication.getId())
            .get();

    // then
    assertThat(updatedMentoringApplication.getMentoringStatus()).isEqualTo(MentoringStatus.CANCELLED);
  }

  @Transactional
  @DisplayName("멘토 아이디와 신청된 멘토링 진행날짜 및 상태 정보들을 이용해 멘토링 상태를 승인합니다.")
  @Test
  void approveMentoringStatus() {
    //given
    Mentor mentor = Mentor.builder()
        .accountName("홍길동")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("random@naver.com")
        .bankName("한국은행")
        .build();
    Mentor savedMentor = mentorRepository.save(mentor);

    Mentoring mentoring = Mentoring.createMentoring(mentor, MentoringBasicInfo.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .build());
    mentoringRepository.save(mentoring);

    User user = User.builder()
        .email("asdf@naver.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("홍홍홍")
        .build();
    User savedUser = userRepository.save(user);

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .startDateTime(
            LocalDateTime.of(2023, 12, 12, 20, 30, 0))
        .endDateTime(
            LocalDateTime.of(2023, 12, 12, 21, 30, 0))
        .user(savedUser)
        .mentoring(mentoring)
        .payment(null)
        .mentoringStatus(MentoringStatus.WAITING)
        .build();
    MentoringApplication savedMentoringApplication = mentoringApplicationRepository.save(mentoringApplication);

    List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos = new ArrayList<>();
    RequiredMentoringStatusInfo requiredMentoringStatusInfo = RequiredMentoringStatusInfo.builder()
        .mentoringReservedTime(
            DateTimeRange.of(
                LocalDateTime.of(
                    2023, 12, 12, 20,
                    30, 0),
                LocalDateTime.of(
                    2023, 12, 12, 21,
                    30, 0)
            ))
        .mentoringStatus(
            MentoringStatus.APPROVAL.name())
        .build();
    requiredMentoringStatusInfos.add(requiredMentoringStatusInfo);

    // when
    mentorService.changeMentoringStatus(savedMentor.getId(), requiredMentoringStatusInfos);
    MentoringApplication updatedMentoringApplication =
        mentoringApplicationRepository.findById(savedMentoringApplication.getId())
            .get();

    // then
    assertThat(updatedMentoringApplication.getMentoringStatus()).isEqualTo(MentoringStatus.APPROVAL);
  }

}