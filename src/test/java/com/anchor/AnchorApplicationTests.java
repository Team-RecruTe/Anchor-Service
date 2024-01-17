package com.anchor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.MentoringService;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;

@SpringBootTest
class AnchorApplicationTests {

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  CacheManager cacheManager;

  @MockBean
  private MentoringRepository mentoringRepository;

  @Autowired
  private MentoringService mentoringService;

  @Test
  @DisplayName("신청자 수를 기준으로 상위 10개의 멘토링을 캐싱해 가져옵니다.")
  void test() throws JsonProcessingException {
    // given
    User user = User.builder()
        .email("email")
        .image("image")
        .role(UserRole.MENTOR)
        .nickname("nickname")
        .build();
    Mentor mentor = Mentor.builder()
        .companyEmail("companyEmail")
        .career(Career.LEADER)
        .accountNumber("1234")
        .accountName("accountName")
        .bankName("bankName")
        .user(user)
        .build();
    Mentoring mentoring = Mentoring.builder()
        .title("title")
        .mentor(mentor)
        .durationTime("1h30m")
        .cost(10000)
        .mentoringDetail(null)
        .build();

    List<MentoringSearchResult> topMentorings = IntStream.range(0, 10)
        .mapToObj(i -> MentoringSearchResult.of(mentoring))
        .toList();

    given(mentoringRepository.findTopMentorings()).willReturn(topMentorings);
    String topMentoring = objectMapper.writeValueAsString(new TopMentoring(topMentorings));

    // when
    IntStream.range(0, 10)
        .forEach(i -> mentoringService.getTopMentorings());
    Cache cache = (Cache) Objects.requireNonNull(cacheManager.getCache("topMentoring"))
        .getNativeCache();
    String cachedTopMentorings = objectMapper.writeValueAsString(cache.asMap()
        .get("topMentoring"));

    // then
    verify(mentoringRepository, times(1)).findTopMentorings();
    assertThat(topMentoring)
        .isEqualTo(cachedTopMentorings);
  }

}



