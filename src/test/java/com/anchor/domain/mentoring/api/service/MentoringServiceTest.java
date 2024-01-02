package com.anchor.domain.mentoring.api.service;

import static com.anchor.domain.mentoring.api.service.TestConstant.APPLICATION_DATE;
import static com.anchor.domain.mentoring.api.service.TestConstant.APPLICATION_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.COST;
import static com.anchor.domain.mentoring.api.service.TestConstant.DURATION_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.FIRST_FROM_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.FIRST_TO_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.MENTORING_CONTENT;
import static com.anchor.domain.mentoring.api.service.TestConstant.MENTORING_TITLE;
import static com.anchor.domain.mentoring.api.service.TestConstant.NICKNAME;
import static com.anchor.domain.mentoring.api.service.TestConstant.SECOND_FROM_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.SECOND_TO_DATE_TIME;
import static com.anchor.domain.mentoring.api.service.TestConstant.USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.service.response.MentoringApplicationResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeResponse;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringUnavailableTimeRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
  @Mock
  private MentoringApplicationRepository mentoringApplicationRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MentoringUnavailableTimeRepository mentoringUnavailableTimeRepository;

  @InjectMocks
  private MentoringService mentoringService;
  private User user;
  private Mentor mentor;

  @BeforeEach
  void testUserAndMentorInit() {

    user = User.builder()
        .email(USER_EMAIL)
        .nickname(NICKNAME)
        .image("testImages")
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

    List<MentoringInfo> result = mentoringService.loadMentoringList();

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

    com.anchor.domain.mentoring.domain.MentoringDetail mentoringDetail = MentoringDetail.builder()
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

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));

    //when
    MentoringDetailResponse result = mentoringService.loadMentoringDetail(inputMentoringId);

    //then
    assertThat(result)
        .extracting("title", "content", "durationTime", "nickname", "cost")
        .contains(MENTORING_TITLE, MENTORING_CONTENT, DURATION_TIME, NICKNAME, COST);
    assertThat(result.getTags())
        .hasSize(mentoringTags.size())
        .contains("자바", "백엔드");
  }

  @Test
  @DisplayName("존재하지 않는 멘토링 ID를 입력하면 NoSuchElementException을 발생시킨다.")
  void loadMentoringDetailFailTest() {
    //given
    Long inputWrongMentoringId = 999L;
    String noSuchElementExceptionMessage = inputWrongMentoringId + "에 해당하는 멘토링이 존재하지 않습니다.";

    given(mentoringRepository.findById(anyLong())).willThrow(
        new NoSuchElementException(noSuchElementExceptionMessage));

    //when then
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
    List<MentoringUnavailableTime> unavailableTimes = createMentoringUnavailableTime(
        mentor);

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));

    //when
    List<MentoringUnavailableTimeResponse> result =
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

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));

    //when
    List<MentoringUnavailableTimeResponse> result =
        mentoringService.loadMentoringUnavailableTime(inputMentoringId);

    //then
    assertThat(result).isNull();
  }

  @Test
  @DisplayName("멘토링 신청이 완료되면 멘토링 신청내역을 저장하고, 성공하면 MentoringApplicationResponse객체를 반환한다.")
  void saveMentoringApplicationIsTrueTest() {
    //given
    SessionUser sessionUser = new SessionUser(user);

    Long mentoringId = 1L;

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(createMentoringApplicationDate())
        .time(createMentoringApplicationTime())
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .mentoring(mentoring)
        .user(user)
        .mentoringApplicationTime(applicationTime)
        .build();

    MentoringUnavailableTime unavailableTime = new MentoringUnavailableTime(mentoringApplication,
        mentoring);

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    given(mentoringApplicationRepository.save(any(MentoringApplication.class)))
        .willReturn(mentoringApplication);

    given(mentoringUnavailableTimeRepository.save(any(MentoringUnavailableTime.class)))
        .willReturn(unavailableTime);

    //when
    MentoringApplicationResponse mentoringApplicationResponse =
        mentoringService.saveMentoringApplication(sessionUser, mentoringId, applicationTime);

    //then
    assertThat(mentoringApplicationResponse)
        .extracting("mentorNickname", "mentoringTitle", "mentoringStartDateTime",
            "mentoringEndDateTime", "mentoringStatus")
        .contains(NICKNAME, MENTORING_TITLE, mentoringApplication.getStartDateTime(),
            mentoringApplication.getEndDateTime(), MentoringStatus.WAITING);
  }

  @Test
  @DisplayName("멘토링 신청내역을 저장하는 도중, 멘토링ID가 잘못되었다면 들어온다면 NoSuchElementException을 반환한다.")
  void saveMentoringApplicationNoFoundMentoringTest() {
    //given
    SessionUser sessionUser = new SessionUser(user);

    Long mentoringId = 1L;

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(createMentoringApplicationDate())
        .time(createMentoringApplicationTime())
        .build();

    given(mentoringRepository.findById(mentoringId)).willThrow(
        new NoSuchElementException(mentoringId + "에 해당하는 멘토링이 존재하지 않습니다."));

    //when then
    assertThatThrownBy(
        () -> mentoringService.saveMentoringApplication(sessionUser, mentoringId, applicationTime))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(mentoringId + "에 해당하는 멘토링이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("멘토링 신청내역을 저장하는 도중, Email을 통해 회원정보를 조회할 수 없다면 NoSuchElementException을 반환한다.")
  void saveMentoringApplicationNotFoundUserTest() {
    //given
    SessionUser sessionUser = new SessionUser(user);

    Long mentoringId = 1L;

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(createMentoringApplicationDate())
        .time(createMentoringApplicationTime())
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();
    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));
    given(userRepository.findByEmail(anyString())).willThrow(
        new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));

    //when then
    assertThatThrownBy(
        () -> mentoringService.saveMentoringApplication(sessionUser, mentoringId, applicationTime))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("멘토링 신청 중인 시간대를 SessionList에 추가한다.")
  void addMentoringApplicationTimeFromSession() {
    //given
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    List<MentoringUnavailableTimeResponse> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .build();

    //when
    mentoringService.addMentoringApplicationTimeFromSession(sessionList, applicationTime);

    //then
    assertThat(sessionList)
        .hasSize(3)
        .extracting("fromDateTime", "toDateTime")
        .containsExactly(
            tuple(LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter)),
            tuple(LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter)),
            tuple(applicationTime.getFromDateTime(), applicationTime.getToDateTime())
        );
  }

  @Test
  @DisplayName("멘토링 신청중인 시간대가 이미 존재한다면, SessionList에 추가하지 않는다.")
  void notAddMentoringApplicationTimeFromSessionIsDuplicate() {
    //given
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    List<MentoringUnavailableTimeResponse> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 2))
        .time(LocalTime.of(13, 0))
        .build();

    //when
    mentoringService.addMentoringApplicationTimeFromSession(sessionList, applicationTime);
    //then
    assertThat(sessionList)
        .hasSize(2)
        .extracting("fromDateTime", "toDateTime")
        .containsExactly(
            tuple(LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter)),
            tuple(LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter))
        );
  }

  @Test
  @DisplayName("멘토링 신청 중 결제 취소나 실패시 잠금되어있던 시간대를 해제한다.")
  void removeMentoringApplicationTimeFromSession() {
    //given
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    List<MentoringUnavailableTimeResponse> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .build();

    sessionList.add(applicationTime.convertToMentoringUnavailableTimeResponse());

    //when
    mentoringService.removeMentoringApplicationTimeFromSession(sessionList, applicationTime);
    //then
    assertThat(sessionList)
        .hasSize(2)
        .extracting("fromDateTime", "toDateTime")
        .containsExactly(
            tuple(LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter)),
            tuple(LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter),
                LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter))
        );
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

  private void fillMentoringTagToMentoring(List<MentoringTag> testTags,
      Mentoring mentoring) {
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

    LocalDateTime fromDateTime2 = LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter);
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

  private LocalDate createMentoringApplicationDate() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    return LocalDate.parse(APPLICATION_DATE, formatter);
  }

  private LocalTime createMentoringApplicationTime() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    return LocalTime.parse(APPLICATION_TIME, formatter);
  }

  private List<MentoringUnavailableTimeResponse> createMentoringUnavailableTimeResponseList() {
    List<MentoringUnavailableTimeResponse> sessionList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    LocalDateTime fromDateTime1 = LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter);
    LocalDateTime toDateTime1 = LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter);

    LocalDateTime fromDateTime2 = LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter);
    LocalDateTime toDateTime2 = LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter);

    MentoringUnavailableTimeResponse response1 = MentoringUnavailableTimeResponse.builder()
        .fromDateTime(fromDateTime1)
        .toDateTime(toDateTime1)
        .build();
    MentoringUnavailableTimeResponse response2 = MentoringUnavailableTimeResponse.builder()
        .fromDateTime(fromDateTime2)
        .toDateTime(toDateTime2)
        .build();

    sessionList.add(response1);
    sessionList.add(response2);

    return sessionList;
  }
}

class TestConstant {

  public static final String NICKNAME = "테스트유저";
  public static final String USER_EMAIL = "testUser@test.com";
  public static final String MENTORING_TITLE = "테스트타이틀";
  public static final String MENTORING_CONTENT = "테스트내용";
  public static final String DURATION_TIME = "1시간";
  public static final Integer COST = 10_000;
  public static final String FIRST_FROM_DATE_TIME = "2024/01/01 00:00:00";
  public static final String FIRST_TO_DATE_TIME = "2024/01/01 23:59:59";
  public static final String SECOND_FROM_DATE_TIME = "2024/01/02 13:00:00";
  public static final String SECOND_TO_DATE_TIME = "2024/01/02 14:00:00";
  public static final String APPLICATION_DATE = "2024/01/03";
  public static final String APPLICATION_TIME = "13:00";
}