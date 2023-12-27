package com.anchor.domain.mentoring.api.service;

import static com.anchor.domain.mentoring.api.service.TestConstant.COST;
import static com.anchor.domain.mentoring.api.service.TestConstant.DURATION_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.FIRST_FROM_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.FIRST_TO_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.MENTORING_CONTENT;
import static com.anchor.domain.mentoring.api.service.TestConstant.MENTORING_TITLE;
import static com.anchor.domain.mentoring.api.service.TestConstant.NICKNAME;
import static com.anchor.domain.mentoring.api.service.TestConstant.SECOND_FROM_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.SECOND_TO_DATE_TIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeDto;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.user.domain.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
class MentoringServiceTest {

  @Mock
  private MentoringRepository mentoringRepository;

  @InjectMocks
  private MentoringService mentoringService;
  private Mentor mentor;

  @BeforeEach
  void testUserAndMentorInit() {

    User user = User.builder()
        .email("testUser@test.com")
        .nickname(NICKNAME)
        .build();

    mentor = Mentor.builder()
        .companyEmail("testCompany@test.com")
        .accountName("계좌명")
        .accountNumber("12345678")
        .bankName("테스트은행")
        .career(Career.MIDDLE)
        .user(user)
        .build();
  }

  @Test
  @DisplayName("전체 멘토링 목록을 조회한다.")
  void loadMentoringListTest() {
    //given
    List<Mentoring> mentoringList = createMentoringAndMentoringList();

    //when
    when(mentoringRepository.findAll()).thenReturn(mentoringList);

    List<MentoringResponseDto> result = mentoringService.loadMentoringList();

    //then
    assertThat(result)
        .hasSize(mentoringList.size())
        .extracting("title", "durationTime", "cost", "nickname")
        .containsExactlyInAnyOrder(
            tuple(MENTORING_TITLE + "1", DURATION_TIME, COST, NICKNAME),
            tuple(MENTORING_TITLE + "2", DURATION_TIME, COST, NICKNAME),
            tuple(MENTORING_TITLE + "3", DURATION_TIME, COST, NICKNAME),
            tuple(MENTORING_TITLE + "4", DURATION_TIME, COST, NICKNAME),
            tuple(MENTORING_TITLE + "5", DURATION_TIME, COST, NICKNAME)
        );
  }

  @Test
  @DisplayName("멘토링 ID를 입력받으면 멘토링 상세정보를 출력한다.")
  void loadMentoringDetailTest() {
    //given
    Long inputMentoringId = 1L;

    MentoringDetail mentoringDetail = MentoringDetail.builder()
        .contents(MENTORING_CONTENT)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .mentoringDetail(mentoringDetail)
        .build();

    List<MentoringTag> mentoringTags = new ArrayList<>();
    fillMentoringTagToMentoring(mentoringTags, mentoring);

    //when
    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.of(mentoring));

    MentoringDetailResponseDto result = mentoringService.loadMentoringDetail(inputMentoringId);

    //then
    assertThat(result)
        .extracting("title", "content", "durationTime", "nickname", "cost")
        .contains(MENTORING_TITLE, MENTORING_CONTENT, DURATION_TIME, NICKNAME, COST);
    assertThat(result.tags())
        .hasSize(mentoringTags.size())
        .contains("자바", "백엔드");
  }

  @Test
  @DisplayName("존재하지 않는 멘토링 ID를 입력하면 NoSuchElementException을 발생시킨다.")
  void loadMentoringDetailFailTest() {
    //given
    Long inputWrongMentoringId = 999L;
    String noSuchElementExceptionMessage = inputWrongMentoringId + "에 해당하는 멘토링이 존재하지 않습니다.";

    //when then
    when(mentoringRepository.findById(anyLong())).thenThrow(
        new NoSuchElementException(noSuchElementExceptionMessage));

    assertThatThrownBy(
        () -> mentoringService.loadMentoringDetail(inputWrongMentoringId)).isInstanceOf(
            NoSuchElementException.class)
        .hasMessage(noSuchElementExceptionMessage);

  }

  @Test
  @DisplayName("멘토링ID를 입력하면 멘토링 불가일자와 시간을 반환한다.")
  void loadMentoringUnavailableTimeTest() {
    //given
    Long inputMentoringId = 1L;
    List<com.anchor.domain.mentoring.domain.MentoringUnavailableTime> unavailableTimes = createMentoringUnavailableTime(
        mentor);

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    //when
    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.of(mentoring));

    List<MentoringUnavailableTimeDto> result =
        mentoringService.loadMentoringUnavailableTime(inputMentoringId);

    //then
    assertThat(result)
        .hasSize(unavailableTimes.size())
        .extracting("fromDateTime", "toDateTime")
        .containsExactly(

            tuple(unavailableTimes.get(0)
                    .getFromDateTime(),
                unavailableTimes.get(0)
                    .getToDateTime()),

            tuple(unavailableTimes.get(1)
                    .getFromDateTime(),
                unavailableTimes.get(1)
                    .getToDateTime())
        );
  }

  @Test
  @DisplayName("멘토링 불가일자가 없다면 null을 반환한다.")
  void loadMentoringUnavailableTimeIsNullTest() {
    //given
    Long inputMentoringId = 1L;

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    //when
    when(mentoringRepository.findById(anyLong())).thenReturn(Optional.of(mentoring));

    List<MentoringUnavailableTimeDto> result =
        mentoringService.loadMentoringUnavailableTime(inputMentoringId);
    //then
    assertThat(result).isNull();
  }

  private List<Mentoring> createMentoringAndMentoringList() {
    List<Mentoring> mentoringList = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      Mentoring testMentoring = Mentoring.builder()
          .title(MENTORING_TITLE + i)
          .durationTime(DURATION_TIME)
          .cost(10_000)
          .mentor(mentor)
          .build();
      mentoringList.add(testMentoring);
    }
    return mentoringList;
  }

  private void fillMentoringTagToMentoring(List<MentoringTag> testTags, Mentoring mentoring) {
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

  private List<MentoringUnavailableTime> createMentoringUnavailableTime(Mentor mentor) {
    List<MentoringUnavailableTime> unavailableTimes = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    LocalDateTime fromDateTime1 = LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter);
    LocalDateTime toDateTime1 = LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter);

    LocalDateTime fromDateTime2 = LocalDateTime.parse(SECOND_FROM_DATE_TIME,
        formatter);
    LocalDateTime toDateTime2 = LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter);

    MentoringUnavailableTime firstUnavailableTime = MentoringUnavailableTime.builder()
        .fromDateTime(fromDateTime1)
        .toDateTime(toDateTime1)
        .mentor(mentor)
        .build();
    MentoringUnavailableTime secondUnavailableTime = MentoringUnavailableTime.builder()
        .fromDateTime(fromDateTime2)
        .toDateTime(toDateTime2)
        .mentor(mentor)
        .build();

    unavailableTimes.add(firstUnavailableTime);
    unavailableTimes.add(secondUnavailableTime);

    return unavailableTimes;
  }
}

class TestConstant {

  public static final String NICKNAME = "테스트유저";
  public static final String MENTORING_TITLE = "테스트타이틀";
  public static final String MENTORING_CONTENT = "테스트내용";
  public static final String DURATION_TIME = "1시간";
  public static final Integer COST = 10_000;
  public static final String FIRST_FROM_DATE_TIME = "2024/01/01 00:00:00";
  public static final String FIRST_TO_DATE_TIME = "2024/01/01 23:59:59";
  public static final String SECOND_FROM_DATE_TIME = "2024/01/02 13:00:00";
  public static final String SECOND_TO_DATE_TIME = "2024/01/02 17:00:00";
}