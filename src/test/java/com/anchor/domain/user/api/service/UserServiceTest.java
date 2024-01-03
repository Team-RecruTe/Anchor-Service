package com.anchor.domain.user.api.service;

import static com.anchor.constant.TestConstant.APPLICATION_DATE;
import static com.anchor.constant.TestConstant.APPLICATION_TIME;
import static com.anchor.constant.TestConstant.DATE_FORMATTER;
import static com.anchor.constant.TestConstant.MENTORING_TITLE;
import static com.anchor.constant.TestConstant.NICKNAME;
import static com.anchor.constant.TestConstant.TIME_FORMATTER;
import static com.anchor.constant.TestConstant.USER_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.PaymentStatus;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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

  @InjectMocks
  UserService userService;

  private User user;
  private SessionUser sessionUser;

  @BeforeEach
  void initUserAndSessionUser() {
    user = User.builder()
        .id(1L)
        .nickname(NICKNAME)
        .email(USER_EMAIL)
        .image("testImage")
        .build();

    sessionUser = new SessionUser(user);
  }

  @Test
  @DisplayName("회원정보를 통해 회원이 신청한 멘토링 목록을 조회한다.")
  void loadAppliedMentoringInfoList() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    List<MentoringApplicationTime> applicationTimeList = createMentoringApplicationTimeList();

    List<MentoringApplication> mentoringApplicationList = createMentoringApplicationList(applicationTimeList, mentoring,
        user);
    List<Payment> paymentList = createPaymentList(mentoringApplicationList);

    List<AppliedMentoringInfo> mentoringInfoList = createMentoringInfoList(mentoringApplicationList, mentoring,
        paymentList);

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    //when
    List<AppliedMentoringInfo> result = userService.loadAppliedMentoringList(sessionUser);

    //then
    assertThat(result)
        .hasSize(5);

    for (int i = 0; i < result.size(); i++) {
      AppliedMentoringInfo mentoringInfo = mentoringInfoList.get(i);
      assertThat(result.get(i)).extracting("startDateTime", "endDateTime", "mentorNickname", "mentoringTitle", "impUid")
          .contains(
              mentoringInfo.getStartDateTime(), mentoringInfo.getEndDateTime(), mentoringInfo.getMentorNickname(),
              mentoringInfo.getMentoringTitle(), mentoringInfo.getImpUid());
    }
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

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    List<MentoringApplicationTime> applicationTimes = createMentoringApplicationTimeList();

    List<MentoringApplication> mentoringApplications = createMentoringApplicationList(applicationTimes, mentoring,
        user);

    List<RequiredMentoringStatusInfo> requiredMentoringStatusInfoList = createMentoringStatus(applicationTimes);

    MentoringStatusInfo changeRequest = MentoringStatusInfo.builder()
        .mentoringStatusList(requiredMentoringStatusInfoList)
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    AtomicInteger index = new AtomicInteger(0);

    given(mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willAnswer(invocation -> {

          int currentIndex = index.getAndIncrement();

          if (currentIndex < mentoringApplications.size()) {

            return Optional.of(mentoringApplications.get(currentIndex));
          } else {

            return null;
          }
        });

    //when
    boolean result = userService.changeAppliedMentoringStatus(sessionUser, changeRequest);

    //then
    assertThat(result).isTrue();
    mentoringApplications.forEach(
        mentoringApplication -> assertThat(mentoringApplication.getMentoringStatus()).isEqualTo(
            MentoringStatus.CANCELED));
  }


  @Test
  @DisplayName("세션에 저장된 회원 이메일정보가 DB에 없을 경우, NoSuchElementException을 발생시킨다.")
  void changeAppliedMentoringStatusNotFoundUserEmail() {
    //given
    RequiredMentoringStatusInfo requiredMentoringStatusInfo = RequiredMentoringStatusInfo.builder()
        .build();

    MentoringStatusInfo changeRequest = MentoringStatusInfo.builder()
        .mentoringStatusList(List.of(requiredMentoringStatusInfo))
        .build();

    given(userRepository.findByEmail(anyString())).willThrow(
        new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));

    //when then
    assertThatThrownBy(
        () -> userService.changeAppliedMentoringStatus(sessionUser, changeRequest))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다.");
  }

  @Test
  @DisplayName("상태변경을 요청하는 멘토링시간대와 일치하는 멘토링 신청내역이 존재하지 않는다면, NoSuchElementException을 발생시킨다.")
  void changeAppliedMentoringStatusNotFoundMentoringApplication() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    List<MentoringApplicationTime> applicationTimes = createMentoringApplicationTimeList();

    List<MentoringApplication> mentoringApplications = createMentoringApplicationList(applicationTimes, mentoring,
        user);

    List<RequiredMentoringStatusInfo> wrongRequiredMentoringStatusInfoList = createWrongAppliedMentoringTimeList(
        applicationTimes);

    MentoringStatusInfo changeRequest = MentoringStatusInfo.builder()
        .mentoringStatusList(wrongRequiredMentoringStatusInfoList)
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    AtomicInteger index = new AtomicInteger(0);

    given(mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willAnswer(invocation -> {
          int currentIndex = index.getAndIncrement();
          if (currentIndex < 3) {
            return Optional.of(mentoringApplications.get(currentIndex));
          } else {
            throw new NoSuchElementException("일치하는 멘토링 신청이력이 존재하지 않습니다.");
          }
        });

    //when
    boolean result = userService.changeAppliedMentoringStatus(sessionUser, changeRequest);

    //then
    assertThat(result).isTrue();
    verify(mentoringApplicationRepository, times(3)).save(any(MentoringApplication.class));
  }


  @Test
  @DisplayName("잘못된 MentoringStatus가 입력되면 IllegalArgumentException을 발생시킨다.")
  void invalidMentoringStatusInput() {
    //given
    Mentor mentor = Mentor.builder()
        .user(user)
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title(MENTORING_TITLE)
        .mentor(mentor)
        .build();

    List<MentoringApplicationTime> applicationTimeList = createMentoringApplicationTimeList();

    List<MentoringApplication> mentoringApplications = createMentoringApplicationList(applicationTimeList, mentoring,
        user);

    List<RequiredMentoringStatusInfo> wrongAppliedMentoringTimeList = createWrongAppliedMentoringStatusList(
        applicationTimeList);

    MentoringStatusInfo changeRequest = MentoringStatusInfo.builder()
        .mentoringStatusList(wrongAppliedMentoringTimeList)
        .build();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    AtomicInteger index = new AtomicInteger(0);

    given(mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willAnswer(invocation -> {
          int currentIndex = index.getAndIncrement();
          if (currentIndex < mentoringApplications.size()) {
            return Optional.of(mentoringApplications.get(currentIndex));
          } else {
            return null;
          }
        });

    //when
    boolean result = userService.changeAppliedMentoringStatus(sessionUser, changeRequest);

    //then
    assertThat(result).isTrue();
    verify(mentoringApplicationRepository, times(3)).save(any(MentoringApplication.class));
  }

  private List<AppliedMentoringInfo> createMentoringInfoList(List<MentoringApplication> mentoringApplicationList,
      Mentoring mentoring, List<Payment> paymentList) {

    List<AppliedMentoringInfo> mentoringInfoList = new ArrayList<>();

    for (int i = 0; i < mentoringApplicationList.size(); i++) {
      mentoringInfoList.add(
          AppliedMentoringInfo.builder()
              .startDateTime(mentoringApplicationList.get(i)
                  .getStartDateTime())
              .endDateTime(mentoringApplicationList.get(i)
                  .getEndDateTime())
              .mentorNickname(user.getNickname())
              .mentoringTitle(mentoring.getTitle())
              .impUid(paymentList.get(i)
                  .getImpUid())
              .build()
      );
    }
    return mentoringInfoList;
  }

  private List<Payment> createPaymentList(List<MentoringApplication> mentoringApplicationList) {
    List<Payment> paymentList = new ArrayList<>();
    for (MentoringApplication mentoringApplication : mentoringApplicationList) {
      paymentList.add(
          Payment.builder()
              .impUid("test_impUid")
              .merchantUid("test_merchantUid")
              .amount(10_000)
              .paymentStatus(PaymentStatus.SUCCESS)
              .mentoringApplication(mentoringApplication)
              .build());
    }
    return paymentList;
  }

  private List<MentoringApplicationTime> createMentoringApplicationTimeList() {

    List<MentoringApplicationTime> applications = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      applications.add(
          MentoringApplicationTime.builder()
              .date(LocalDate.parse(APPLICATION_DATE, DATE_FORMATTER)
                  .plusDays(i))
              .time(LocalTime.parse(APPLICATION_TIME, TIME_FORMATTER))
              .build());
    }

    return applications;
  }

  private List<MentoringApplication> createMentoringApplicationList
      (List<MentoringApplicationTime> applicationTimes, Mentoring mentoring, User user) {

    List<MentoringApplication> mentoringApplications = new ArrayList<>();

    for (MentoringApplicationTime applicationTime : applicationTimes) {

      mentoringApplications.add(
          MentoringApplication.builder()
              .mentoring(mentoring)
              .user(user)
              .mentoringStatus(MentoringStatus.APPROVAL)
              .mentoringApplicationTime(applicationTime)
              .build());
    }

    return mentoringApplications;
  }

  private List<RequiredMentoringStatusInfo> createMentoringStatus
      (List<MentoringApplicationTime> applicationTimeList) {

    List<RequiredMentoringStatusInfo> mentoringStatusList = new ArrayList<>();

    for (MentoringApplicationTime applicationTime : applicationTimeList) {
      mentoringStatusList.add(
          RequiredMentoringStatusInfo.builder()
              .status(MentoringStatus.CANCELED)
              .startDateTime(applicationTime.getFromDateTime())
              .endDateTime(applicationTime.getToDateTime())
              .build()
      );
    }

    return mentoringStatusList;
  }

  private List<RequiredMentoringStatusInfo> createWrongAppliedMentoringTimeList
      (List<MentoringApplicationTime> applicationTimes) {

    LocalDateTime now = LocalDateTime.now();

    List<RequiredMentoringStatusInfo> statusList = new ArrayList<>();

    for (int i = 0; i < 5; i++) {
      LocalDateTime startDateTime = now;

      if (i <= 2) {
        startDateTime = applicationTimes.get(i)
            .getFromDateTime();

      }

      statusList.add(
          RequiredMentoringStatusInfo.builder()
              .status(MentoringStatus.CANCELED)
              .startDateTime(startDateTime)
              .endDateTime(startDateTime.plusHours(1L))
              .build()
      );
    }
    return statusList;
  }

  private List<RequiredMentoringStatusInfo> createWrongAppliedMentoringStatusList(
      List<MentoringApplicationTime> applicationTimes) {
    List<RequiredMentoringStatusInfo> statusList = new ArrayList<>();

    for (int i = 0; i < 5; i++) {

      LocalDateTime startDateTime = applicationTimes.get(i)
          .getFromDateTime();

      statusList.add(
          RequiredMentoringStatusInfo.builder()
              .status(i <= 2 ? MentoringStatus.CANCELED : MentoringStatus.WAITING)
              .startDateTime(startDateTime)
              .endDateTime(startDateTime.plusHours(1L))
              .build()
      );
    }
    return statusList;
  }
}