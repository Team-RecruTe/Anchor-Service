package com.anchor.domain.mentor.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.config.QueryDslConfig;
import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.Slf4JLoggerFactory;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("멘토 서비스 테스트 - DB 의존성 포함")
@ActiveProfiles("test")
@Import({QueryDslConfig.class, MentorService.class, ObjectMapper.class})
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
  PaymentRepository paymentRepository;

  @Autowired
  MentorService mentorService;

  @Autowired
  ObjectMapper objectMapper;

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

  @Transactional
  @DisplayName("검색 조건을 통해 멘토링 신청 목록을 조회합니다.")
  @Test
  void getMentoringApplications() throws JsonProcessingException {
    // given
    User mentorUser = User.builder()
        .email("qwer@naver.com")
        .nickname("홍길동")
        .role(UserRole.MENTOR)
        .build();
    User savedMentorUser = userRepository.save(mentorUser);

    User menteeUser = User.builder()
        .email("qwer@naver.com")
        .nickname("홍길동")
        .role(UserRole.MENTOR)
        .build();
    User savedMenteeUser = userRepository.save(menteeUser);

    Mentor mentor = Mentor.builder()
        .companyEmail("company@navercorp.com")
        .accountName("홍길동")
        .accountNumber("123412341234")
        .bankName("한국은행")
        .career(Career.MIDDLE)
        .user(savedMentorUser)
        .build();
    Mentor savedMentor = mentorRepository.save(mentor);

    List<String> tags = List.of("java", "spring", "docker", "redis", "data");
    Mentoring mentoring = Mentoring.builder()
        .title("제목")
        .durationTime("1h")
        .cost(10000)
        .mentor(savedMentor)
        .build();
    mentoring.editContents(MentoringContentsInfo.builder()
        .contents("하이용, 내용입니다.")
        .tags(tags)
        .build());
    Mentoring savedMentoring = mentoringRepository.save(mentoring);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime time = now;
    List<Payment> payments = new ArrayList<>();
    for (int i = 0; i < 30; i++) {
      time = time.plusHours(i);
      MentoringApplication mentoringApplication = MentoringApplication.builder()
          .user(savedMenteeUser)
          .mentoring(savedMentoring)
          .startDateTime(time)
          .endDateTime(time.plusHours(1))
          .build();
      Payment payment = Payment.builder()
          .mentoringApplication(mentoringApplication)
          .amount(10000)
          .impUid(UUID.randomUUID()
              .toString())
          .merchantUid("toss_" + UUID.randomUUID())
          .build();
      mentoringApplication.connectPayment(payment);
      paymentRepository.save(payment);
      mentoringApplicationRepository.save(mentoringApplication);
    }

    // when
    PageRequest pageRequest = PageRequest.of(0, 10, Direction.DESC, "id");
    Page<AppliedMentoringSearchResult> results = mentorService.getMentoringApplications(
        savedMentor.getId(),
        pageRequest);

    InternalLogger log = Slf4JLoggerFactory.getInstance("TestLog");
    String result = objectMapper.writeValueAsString(results);
    log.info("{}", result);

    // then
    assertThat(results.getTotalElements()).isEqualTo(10);
  }

}