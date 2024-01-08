package com.anchor.domain.mentoring.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentoringViewServiceTest {

  @Mock
  private MentoringRepository mentoringRepository;

  @InjectMocks
  private MentoringViewService mentoringViewService;
  private User testUser;
  private Mentor testMentor;

  @BeforeEach
  void testUserAndMentorInit() {
    testUser = User.builder()
        .email("testUser@test.com")
        .nickname("테스트유저")
        .build();
    testMentor = Mentor.builder()
        .companyEmail("testCompany@test.com")
        .accountName("계좌명")
        .accountNumber("12345678")
        .bankName("테스트은행")
        .career(Career.MIDDLE)
        .user(testUser)
        .build();
  }

  @Test
  @DisplayName("전체 멘토링 목록을 조회한다.")
  void loadMentoringListTest() {
    //given
    List<Mentoring> mentoringList = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      Mentoring testMentoring = Mentoring.builder()
          .title("테스트타이틀" + i)
          .durationTime("1시간")
          .cost(10_000)
          .mentor(testMentor)
          .build();
      mentoringList.add(testMentoring);
    }

    //when
    when(mentoringRepository.findAll()).thenReturn(mentoringList);
    List<MentoringResponseDto> result = mentoringViewService.loadMentoringList();

    //then
    assertThat(result).hasSize(mentoringList.size())
        .extracting("title", "durationTime", "cost", "nickname")
        .containsExactlyInAnyOrder(
            tuple("테스트타이틀1", "1시간", 10_000, testUser.getNickname()),
            tuple("테스트타이틀2", "1시간", 10_000, testUser.getNickname()),
            tuple("테스트타이틀3", "1시간", 10_000, testUser.getNickname()),
            tuple("테스트타이틀4", "1시간", 10_000, testUser.getNickname()),
            tuple("테스트타이틀5", "1시간", 10_000, testUser.getNickname())
        );
  }

  @Test
  @DisplayName("멘토링 ID를 입력받으면 멘토링 상세정보를 출력한다.")
  void loadMentoringDetailTest() {
    //given
    Long id = 1L;

    MentoringDetail testDetail = MentoringDetail.builder()
        .contents("테스트내용")
        .build();
    Mentoring testMentoring = Mentoring.builder()
        .title("테스트타이틀")
        .durationTime("1시간")
        .cost(10_000)
        .mentor(testMentor)
        .mentoringDetail(testDetail)
        .build();
    List<MentoringTag> testTags = new ArrayList<>();
    fillTags(testTags, testMentoring);
    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.of(testMentoring));

    //when
    MentoringDetailResponseDto result = mentoringViewService.loadMentoringDetail(id);

    //then
    assertThat(result).extracting("title", "content", "durationTime", "nickname", "cost")
        .contains("테스트타이틀", "테스트내용", "1시간", "테스트유저", 10_000);
    assertThat(result.tags()).hasSize(2)
        .contains("자바", "백엔드");
  }

  @Test
  @DisplayName("존재하지 않는 멘토링 ID를 입력하면 예외를 발생시킨다.")
  void loadMentoringDetailFailureTest() {
    //given
    Long id = 999L;
    String exceptionMessage = "잘못된 출력입니다.";
    when(mentoringRepository.findById(anyLong())).thenThrow(
        new NoSuchElementException(exceptionMessage));

    //when then
    assertThatThrownBy(() -> mentoringViewService.loadMentoringDetail(id)).isInstanceOf(
            NoSuchElementException.class)
        .hasMessage(exceptionMessage);
    
  }

  private void fillTags(List<MentoringTag> testTags, Mentoring mentoring) {
    MentoringTag backEndTag = MentoringTag.builder()
        .tag("백엔드")
        .mentoring(mentoring)
        .build();
    MentoringTag javaTag = MentoringTag.builder()
        .tag("자바")
        .mentoring(mentoring)
        .build();
    testTags.add(backEndTag);
    testTags.add(javaTag);
  }
}
