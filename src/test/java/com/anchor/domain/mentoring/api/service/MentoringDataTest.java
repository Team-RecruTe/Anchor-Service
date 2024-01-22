package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.config.QueryDslConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.internal.logging.InternalLogger;
import io.micrometer.common.util.internal.logging.Slf4JLoggerFactory;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
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

@DisplayName("멘토링 서비스 테스트 - DB 의존성 포함")
@Import({QueryDslConfig.class, MentoringService.class, ObjectMapper.class, PayNumberCreator.class})
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

  @Transactional
  @DisplayName("멘토링 아이디를 이용해 멘토링 상세 내용과 태그들을 등록합니다.")
  @Test
  void registerMentoringContents() {
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

    List<String> tags = List.of("java", "spring");

    // when
//    mentoringService.editContents(savedMentoring.getId(), new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>", tags));
    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
        .get();

    // then
    MentoringDetail mentoringDetail = updatedMentoring.getMentoringDetail();
    Set<MentoringTag> mentoringTags = updatedMentoring.getMentoringTags();

    assertThat(mentoringDetail)
        .extracting("contents")
        .isEqualTo("<h1>컨텐츠입니다.<h1>");

    assertThat(mentoringTags)
        .extracting("tag")
        .contains("java", "spring");
  }

  @Transactional
  @DisplayName("멘토링 아이디를 이용해 멘토링 상세 내용과 태그들을 수정합니다.")
  @Test
  void editMentoringContents() {
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

    // when
//    mentoringService.editContents(savedMentoring.getId(),
//        new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>", List.of("java", "spring")));
//    mentoringService.editContents(savedMentoring.getId(),
//        new MentoringContentsInfo("<h1>수정된 컨텐츠입니다.<h1>", List.of("java", "spring", "boot")));
    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
        .get();

    // then
    MentoringDetail mentoringDetail = updatedMentoring.getMentoringDetail();
    Set<MentoringTag> mentoringTags = updatedMentoring.getMentoringTags();

    assertThat(mentoringDetail)
        .extracting("contents")
        .isEqualTo("<h1>수정된 컨텐츠입니다.<h1>");

    assertThat(mentoringTags)
        .extracting("tag")
        .contains("java", "spring", "boot");
  }

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

//    mentoringService.editContents(savedMentoring.getId(),
//        new MentoringContentsInfo("<h1>컨텐츠입니다.<h1>", List.of("java", "spring")));

    Mentoring updatedMentoring = mentoringRepository.findById(savedMentoring.getId())
        .get();

    // when
    mentoringService.delete(updatedMentoring.getId());

    // then
    assertThat(mentoringRepository.findById(updatedMentoring.getId()))
        .isEmpty();
  }

  @Transactional
  @DisplayName("검색 조건을 통해 멘토링 목록을 조회합니다.")
  @Test
  void testName() throws JsonProcessingException {
    // given
    User user = User.builder()
        .email("qwer@naver.com")
        .nickname("홍길동")
        .role(UserRole.MENTOR)
        .build();
    User savedUser = userRepository.save(user);

    Mentor mentor = Mentor.builder()
        .companyEmail("company@navercorp.com")
        .accountName("홍길동")
        .accountNumber("123412341234")
        .bankName("한국은행")
        .career(Career.MIDDLE)
        .user(savedUser)
        .build();
    Mentor savedMentor = mentorRepository.save(mentor);

    List<String> tagList = List.of("java", "spring", "docker", "redis", "data");
    List<Mentoring> mentorings = new ArrayList<>();
    for (int count = 1; count < 50; count++) {
      int skip = count % 5;
      List<String> tags = IntStream.range(0, 5)
          .filter(i -> i != skip)
          .mapToObj(tagList::get)
          .toList();

      Mentoring mentoring = Mentoring.builder()
          .title("제목" + count)
          .durationTime("1h")
          .cost(10000)
          .mentor(savedMentor)
          .build();

      mentoring.editContents(MentoringContentsInfo.builder()
          .contents("하이용, 내용입니다." + count)
          .tags(tags)
          .build());
      mentorings.add(mentoring);
    }
    mentoringRepository.saveAll(mentorings);

    // when
    PageRequest pageRequest = PageRequest
        .of(0, 16, Direction.DESC, "id", "totalApplicationNumber");
    Page<MentoringSearchResult> results = mentoringService.getMentorings(List.of("java", "docker"), "제목",
        pageRequest);

    // then
    assertThat(results.getTotalElements()).isEqualTo(16);
    InternalLogger log = Slf4JLoggerFactory.getInstance("TestLog");
    String result = objectMapper.writeValueAsString(results);
    log.info("{}", result);

  }

}
