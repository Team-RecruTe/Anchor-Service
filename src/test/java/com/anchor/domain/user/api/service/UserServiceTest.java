package com.anchor.domain.user.api.service;

import static com.anchor.constant.TestConstant.APPLICATION_DATE;
import static com.anchor.constant.TestConstant.APPLICATION_TIME;
import static com.anchor.constant.TestConstant.DATE_FORMATTER;
import static com.anchor.constant.TestConstant.FIRST_FROM_DATE_TIME;
import static com.anchor.constant.TestConstant.FIRST_TO_DATE_TIME;
import static com.anchor.constant.TestConstant.MENTORING_TITLE;
import static com.anchor.constant.TestConstant.NICKNAME;
import static com.anchor.constant.TestConstant.SECOND_FROM_DATE_TIME;
import static com.anchor.constant.TestConstant.SECOND_TO_DATE_TIME;
import static com.anchor.constant.TestConstant.TIME_FORMATTER;
import static com.anchor.constant.TestConstant.USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringUnavailableTimeRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.PaymentStatus;
import com.anchor.domain.user.api.controller.request.AppliedMentoringStatus;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
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
class UserServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  MentoringApplicationRepository mentoringApplicationRepository;
  @Mock
  MentoringUnavailableTimeRepository mentoringUnavailableTimeRepository;

  @InjectMocks
  UserService userService;

  private User user;
  private SessionUser sessionUser;

  @BeforeEach
  void initUserAndSessionUser() {
    user = User.builder()
        .nickname(NICKNAME)
        .email(USER_EMAIL)
        .image("testImage")
        .build();

    sessionUser = new SessionUser(user);
  }

  @Test
  @DisplayName("멘토링 신청내역을 조회한다.")
  void loadAppliedMentoringList() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    MentoringApplicationTime applicationTime = createMentoringApplicationTime();

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .user(user)
        .build();

    Payment payment = Payment.builder()
        .impUid("test_impUid")
        .merchantUid("test_merchantUid")
        .amount(10_000)
        .paymentStatus(PaymentStatus.SUCCESS)
        .mentoringApplication(mentoringApplication)
        .build();

    AppliedMentoringInfo appliedMentoringInfo = AppliedMentoringInfo.builder()
        .startDateTime(mentoringApplication.getStartDateTime())
        .endDateTime(mentoringApplication.getEndDateTime())
        .mentorNickname(user.getNickname())
        .mentoringTitle(mentoring.getTitle())
        .impUid(payment.getImpUid())
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when
    List<AppliedMentoringInfo> result = userService.loadAppliedMentoringList(sessionUser);

    //then
    assertThat(result)
        .hasSize(1)
        .extracting("startDateTime", "endDateTime", "mentorNickname", "mentoringTitle", "impUid")
        .contains(
            tuple(appliedMentoringInfo.getStartDateTime(), appliedMentoringInfo.getEndDateTime(),
                appliedMentoringInfo.getMentorNickname(), appliedMentoringInfo.getMentoringTitle(),
                appliedMentoringInfo.getImpUid())
        );
  }


  @Test
  @DisplayName("멘토링 신청내역이 없다면, null을 반환한다.")
  void loadAppliedMentoringIsNullList() {
    //given
    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when
    List<AppliedMentoringInfo> result = userService.loadAppliedMentoringList(sessionUser);

    //then
    assertThat(result).isNull();
  }


  @Test
  @DisplayName("멘토링 신청내역의 상태변경요청이 취소로 전달되면 상태를 변경하고, 멘토링 불가시간에서 삭제한다.")
  void changeAppliedMentoringStatus() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    List<MentoringUnavailableTime> unavailableTimeList = createMentoringUnavailableTime(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    MentoringApplicationTime applicationTime = createMentoringApplicationTime();

    unavailableTimeList.add(applicationTime.convertToMentoringUnavailableTime(mentor));

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .mentoringStatus(MentoringStatus.CANCELED)
        .user(user)
        .build();

    AppliedMentoringStatus appliedMentoringStatus = createAppliedMentoringStatusWithStatusIsCANCELED(applicationTime);

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    given(mentoringApplicationRepository.save(any(MentoringApplication.class))).willReturn(mentoringApplication);

    //when
    boolean result = userService.changeAppliedMentoringStatus(sessionUser, appliedMentoringStatus);

    //then
    assertThat(result).isTrue();
    verify(mentoringUnavailableTimeRepository, times(1)).delete(any(MentoringUnavailableTime.class));
  }

  @Test
  @DisplayName("세션에 저장된 회원 이메일정보가 DB에 없을 경우, NoSuchElementException을 발생시킨다.")
  void changeAppliedMentoringStatusNotFoundUserEmail() {
    //given
    AppliedMentoringStatus appliedMentoringStatus = AppliedMentoringStatus.builder()
        .build();

    given(userRepository.findByEmail(anyString())).willThrow(
        new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));

    //when then
    assertThatThrownBy(
        () -> userService.changeAppliedMentoringStatus(sessionUser, appliedMentoringStatus))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다.");

    verify(mentoringUnavailableTimeRepository, times(0)).delete(any(MentoringUnavailableTime.class));
  }

  @Test
  @DisplayName("입력된 멘토링상태변경 시간대와 일치하는 멘토링신청이력이 없을경우, 예외를 발생시킨다.")
  void changeAppliedMentoringStatusNotFoundMentoringApplication() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    List<MentoringUnavailableTime> unavailableTimeList = createMentoringUnavailableTime(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    MentoringApplicationTime applicationTime = createMentoringApplicationTime();

    unavailableTimeList.add(applicationTime.convertToMentoringUnavailableTime(mentor));

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .mentoringStatus(MentoringStatus.CANCELED)
        .user(user)
        .build();

    AppliedMentoringStatus wrongAppliedMentoringStatus = createWrongAppliedMentoringStatus();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when then
    assertThatThrownBy(
        () -> userService.changeAppliedMentoringStatus(sessionUser, wrongAppliedMentoringStatus))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("일치하는 멘토링 신청이력을 조회하지 못했습니다.");

    verify(mentoringUnavailableTimeRepository, times(0)).delete(any(MentoringUnavailableTime.class));
  }


  @Test
  @DisplayName("잘못된 MentoringStatus가 입력되면 IllegalArgumentException을 발생시킨다.")
  void invalidMentoringStatusInput() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    List<MentoringUnavailableTime> unavailableTimeList = createMentoringUnavailableTime(mentor);

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    MentoringApplicationTime applicationTime = createMentoringApplicationTime();

    unavailableTimeList.add(applicationTime.convertToMentoringUnavailableTime(mentor));

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .mentoringStatus(MentoringStatus.CANCELED)
        .user(user)
        .build();

    AppliedMentoringStatus appliedMentoringStatus = createAppliedMentoringStatusWithStatusIsWAITING(applicationTime);

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when then
    assertThatThrownBy(
        () -> userService.changeAppliedMentoringStatus(sessionUser, appliedMentoringStatus))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("잘못된 상태변경입니다.");

    verify(mentoringUnavailableTimeRepository, times(0)).delete(any(MentoringUnavailableTime.class));
  }

  private MentoringApplicationTime createMentoringApplicationTime() {
    return MentoringApplicationTime.builder()
        .date(LocalDate.parse(APPLICATION_DATE, DATE_FORMATTER))
        .time(LocalTime.parse(APPLICATION_TIME, TIME_FORMATTER))
        .build();
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

  private AppliedMentoringStatus createAppliedMentoringStatusWithStatusIsCANCELED(
      MentoringApplicationTime applicationTime) {

    return AppliedMentoringStatus.builder()
        .status(MentoringStatus.CANCELED)
        .startDateTime(applicationTime.getFromDateTime())
        .endDateTime(applicationTime.getToDateTime())
        .build();
  }

  private AppliedMentoringStatus createAppliedMentoringStatusWithStatusIsWAITING(
      MentoringApplicationTime applicationTime) {

    return AppliedMentoringStatus.builder()
        .status(MentoringStatus.WAITING)
        .startDateTime(applicationTime.getFromDateTime())
        .endDateTime(applicationTime.getToDateTime())
        .build();
  }

  private AppliedMentoringStatus createWrongAppliedMentoringStatus() {
    LocalDateTime now = LocalDateTime.now();
    return AppliedMentoringStatus.builder()
        .status(MentoringStatus.CANCELED)
        .startDateTime(now)
        .endDateTime(now.plusHours(1L))
        .build();
  }

}