package com.anchor.domain.mentoring.api.service;

import static com.anchor.constant.TestConstant.COST;
import static com.anchor.constant.TestConstant.DURATION_TIME;
import static com.anchor.constant.TestConstant.FIRST_FROM_DATE_TIME;
import static com.anchor.constant.TestConstant.FIRST_TO_DATE_TIME;
import static com.anchor.constant.TestConstant.MENTORING_CONTENT;
import static com.anchor.constant.TestConstant.MENTORING_TITLE;
import static com.anchor.constant.TestConstant.NICKNAME;
import static com.anchor.constant.TestConstant.SECOND_FROM_DATE_TIME;
import static com.anchor.constant.TestConstant.SECOND_TO_DATE_TIME;
import static com.anchor.constant.TestConstant.USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.response.ApplicationUnavailableTime;
import com.anchor.domain.mentoring.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
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
  private PaymentRepository paymentRepository;
  @Mock
  private UserRepository userRepository;

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
  @DisplayName("멘토링 ID를 입력받으면 멘토링 상세정보를 출력한다.")
  void loadMentoringDetailTest() {
    //given
    Long inputMentoringId = 1L;

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    mentoring.editContents(new MentoringContentsInfo("테스트내용", List.of("백엔드", "자바")));

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));

    //when
    MentoringDetailInfo result = mentoringService.loadMentoringDetail(inputMentoringId);

    //then
    assertThat(result)
        .extracting("title", "content", "durationTime", "nickname", "cost")
        .contains(MENTORING_TITLE, MENTORING_CONTENT, DURATION_TIME, NICKNAME, COST);
    assertThat(result.getTags())
        .hasSize(2)
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
  @DisplayName("멘토링 신청이 완료되면 멘토링 신청내역을 저장하고, 성공하면 AppliedMentoringInfo객체를 반환한다.")
  void saveMentoringApplicationIsTrueTest() {
    //given
    SessionUser sessionUser = new SessionUser(user);

    Long mentoringId = 1L;

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .durationTime(DURATION_TIME)
        .cost(10_000)
        .mentor(mentor)
        .build();

    MentoringApplicationInfo applicationInfo = MentoringApplicationInfo.builder()
        .amount(10_000)
        .impUid("testImpUid")
        .merchantUid("test_Merchant")
        .startDateTime(LocalDateTime.of(2024, 1, 3, 13, 0, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 3, 14, 0, 0))
        .build();

    MentoringApplication mentoringApplication = new MentoringApplication(applicationInfo, null, mentoring,
        Payment.builder()
            .amount(10_000)
            .merchantUid("toss_testMerchant")
            .build(), user);

    given(mentoringRepository.findById(anyLong())).willReturn(Optional.of(mentoring));
    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when
    AppliedMentoringInfo appliedMentoringInfo =
        mentoringService.saveMentoringApplication(sessionUser, mentoringId, applicationInfo);

    //then
    assertThat(appliedMentoringInfo)
        .extracting("mentorNickname", "mentoringTitle", "mentoringStartDateTime",
            "mentoringEndDateTime", "mentoringStatus")
        .contains(NICKNAME, MENTORING_TITLE, mentoringApplication.getStartDateTime(),
            mentoringApplication.getEndDateTime(), MentoringStatus.WAITING);
  }

  @Test
  @DisplayName("멘토링 신청내역을 저장하는 도중, Email을 통해 회원정보를 조회할 수 없다면 NoSuchElementException을 반환한다.")
  void saveMentoringApplicationNotFoundUserTest() {
    //given
    SessionUser sessionUser = new SessionUser(user);

    Long mentoringId = 1L;

    MentoringApplicationInfo applicationInfo = MentoringApplicationInfo.builder()
        .amount(10_000)
        .impUid("testImpUid")
        .merchantUid("testMerchant")
        .startDateTime(LocalDateTime.of(2024, 1, 3, 13, 0, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 3, 14, 0, 0))
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
        () -> mentoringService.saveMentoringApplication(sessionUser, mentoringId, applicationInfo))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("멘토링 신청 중인 시간대를 SessionList에 추가한다.")
  void addMentoringApplicationTimeFromSession() {
    //given
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    List<ApplicationUnavailableTime> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .durationTime("1h30m")
        .build();

    //when
    mentoringService.addApplicationTimeFromSession(sessionList, applicationTime);

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
    List<ApplicationUnavailableTime> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 2))
        .time(LocalTime.of(13, 0))
        .durationTime("1h")
        .build();

    //when
    mentoringService.addApplicationTimeFromSession(sessionList, applicationTime);
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
    List<ApplicationUnavailableTime> sessionList = createMentoringUnavailableTimeResponseList();

    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .durationTime("1h")
        .build();

    sessionList.add(applicationTime.convertToMentoringUnavailableTimeResponse());

    //when
    mentoringService.removeApplicationTimeFromSession(sessionList, applicationTime);
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

  private List<ApplicationUnavailableTime> createMentoringUnavailableTimeResponseList() {
    List<ApplicationUnavailableTime> sessionList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    LocalDateTime fromDateTime1 = LocalDateTime.parse(FIRST_FROM_DATE_TIME, formatter);
    LocalDateTime toDateTime1 = LocalDateTime.parse(FIRST_TO_DATE_TIME, formatter);

    LocalDateTime fromDateTime2 = LocalDateTime.parse(SECOND_FROM_DATE_TIME, formatter);
    LocalDateTime toDateTime2 = LocalDateTime.parse(SECOND_TO_DATE_TIME, formatter);

    ApplicationUnavailableTime response1 = ApplicationUnavailableTime.builder()
        .fromDateTime(fromDateTime1)
        .toDateTime(toDateTime1)
        .build();
    ApplicationUnavailableTime response2 = ApplicationUnavailableTime.builder()
        .fromDateTime(fromDateTime2)
        .toDateTime(toDateTime2)
        .build();

    sessionList.add(response1);
    sessionList.add(response2);

    return sessionList;
  }

}