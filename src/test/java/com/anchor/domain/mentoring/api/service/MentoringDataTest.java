package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.config.QueryDslConfig;
import com.anchor.global.config.RedisConfig;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.redis.client.ApplicationLockClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("멘토링 서비스 테스트 - DB 의존성 포함")
@Import({QueryDslConfig.class, MentoringService.class, ObjectMapper.class, ApplicationLockClient.class,
    RedisConfig.class, AsyncMailSender.class, JavaMailSenderImpl.class})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
public class MentoringDataTest {

  @Autowired
  UserRepository userRepository;

  @Autowired
  MentoringService mentoringService;

  @Autowired
  MentoringRepository mentoringRepository;

  @Autowired
  MentorRepository mentorRepository;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  EntityManager entityManager;

//  @Transactional
//  @DisplayName("멘토링 아이디를 이용해 멘토링 상세 내용과 태그들을 등록합니다.")
//  @Test
//  void registerMentoringContents() {
//    // given
//    Mentor mentor = Mentor.builder()
//        .accountName("홍길동")
//        .career(Career.JUNIOR)
//        .accountNumber("123456")
//        .companyEmail("random@naver.com")
//        .bankName("한국은행")
//        .build();
//    mentorRepository.save(mentor);
//
//    Mentoring mentoring = Mentoring.builder()
//        .title("제목입니다.")
//        .durationTime("1h")
//        .cost(10000)
//        .mentor(mentor)
//        .build();
//    Mentoring savedMentoring = mentoringRepository.save(mentoring);
//
//    List<String> tags = List.of("java", "spring");
//
//    // when
////    mentoringService.editContents(savedMentoring.getId(), new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>", tags));
//    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
//        .get();
//
//    // then
//    MentoringDetail mentoringDetail = updatedMentoring.getMentoringDetail();
//    Set<MentoringTag> mentoringTags = updatedMentoring.getMentoringTags();
//
//    assertThat(mentoringDetail)
//        .extracting("contents")
//        .isEqualTo("<h1>컨텐츠입니다.<h1>");
//
//    assertThat(mentoringTags)
//        .extracting("tag")
//        .contains("java", "spring");
//  }

//  @Transactional
//  @DisplayName("멘토링 아이디를 이용해 멘토링 상세 내용과 태그들을 수정합니다.")
//  @Test
//  void editMentoringContents() {
//    // given
//    Mentor mentor = Mentor.builder()
//        .accountName("홍길동")
//        .career(Career.JUNIOR)
//        .accountNumber("123456")
//        .companyEmail("random@naver.com")
//        .bankName("한국은행")
//        .build();
//    mentorRepository.save(mentor);
//
//    Mentoring mentoring = Mentoring.builder()
//        .title("제목입니다.")
//        .durationTime("1h")
//        .cost(10000)
//        .mentor(mentor)
//        .build();
//    Mentoring savedMentoring = mentoringRepository.save(mentoring);
//
//    // when
////    mentoringService.editContents(savedMentoring.getId(),
////        new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>", List.of("java", "spring")));
////    mentoringService.editContents(savedMentoring.getId(),
////        new MentoringContentsInfo("<h1>수정된 컨텐츠입니다.<h1>", List.of("java", "spring", "boot")));
//    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
//        .get();
//
//    // then
//    MentoringDetail mentoringDetail = updatedMentoring.getMentoringDetail();
//    Set<MentoringTag> mentoringTags = updatedMentoring.getMentoringTags();
//
//    assertThat(mentoringDetail)
//        .extracting("contents")
//        .isEqualTo("<h1>수정된 컨텐츠입니다.<h1>");
//
//    assertThat(mentoringTags)
//        .extracting("tag")
//        .contains("java", "spring", "boot");
//  }

  @Transactional
  @DisplayName("멘토링 아이디를 이용해 멘토링과 연관 있는 멘토링 상세 내용과 태그들을 함께 삭제합니다.")
  @Test
  void deleteMentoring() {
    // given
    Mentor mentor = Mentor.builder()
        .accountName("홍길동")
        .career(Career.JUNIOR)
        .accountNumber("123456")
        .companyEmail("random@naver.com")
        .bankName("한국은행")
        .build();
    mentorRepository.save(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title("제목입니다.")
        .durationTime("1h")
        .cost(10000)
        .mentor(mentor)
        .build();
    Mentoring savedMentoring = mentoringRepository.save(mentoring);

    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
        .get();

    // when
    mentoringService.delete(updatedMentoring.getId());

    // then
    assertThat(mentoringRepository.findById(updatedMentoring.getId()))
        .isEmpty();
  }

}
