package com.anchor.domain.mentor.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.controller.request.PayupMonthRange;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.payment.domain.PayupStatus;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.type.mentor.DuplicateEmailException;
import com.anchor.global.nhpay.PayupCalculator;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

  @InjectMocks
  private MentorService mentorService;

  @Mock
  private MentorRepository mentorRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PayupRepository payupRepository;

  @Test
  @DisplayName("멘토 등록 성공")
  void registerSuccess() {
    User user = User.builder()
        .email("test@test.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("nickname")
        .build();
    SessionUser sessionUser = new SessionUser(user);
    Mentor mentor = Mentor.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    when(mentorRepository.save(any(Mentor.class)))
        .thenReturn(mentor);
    when(mentorRepository.existsMentorByCompanyEmail(anyString()))
        .thenReturn(false);
    when(userRepository.findByEmail(anyString()))
        .thenReturn(Optional.of(user));

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("00000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();
    Mentor registerResult = mentorService.register(mentorRegisterInfo, sessionUser);

    assertThat(registerResult.getCompanyEmail())
        .isEqualTo(mentor.getCompanyEmail());
  }

  @Test
  @DisplayName("멘토 등록 실패")
  void registerFail() {
    User user = User.builder()
        .email("test@test.com")
        .image("image")
        .role(UserRole.USER)
        .nickname("nickname")
        .build();
    SessionUser sessionUser = new SessionUser(user);

    when(mentorRepository.existsMentorByCompanyEmail(anyString()))
        .thenReturn(true);

    MentorRegisterInfo mentorRegisterInfo = MentorRegisterInfo.builder()
        .bankName("NH농협")
        .accountNumber("020-3039-765")
        .companyEmail("0000@naver.com")
        .accountName("leejuyoon")
        .career(Career.JUNIOR)
        .build();

    assertThatThrownBy(() ->
        mentorService.register(mentorRegisterInfo, sessionUser))
        .isInstanceOf(DuplicateEmailException.class);
  }

  @Test
  @DisplayName("정산내역을 조회합니다.")
  void getPayupListTest() {
    //given
    PayupMonthRange monthRange = PayupMonthRange.builder()
        .startMonth(LocalDateTime.of(2023, 6, 1, 0, 0, 0))
        .currentMonth(LocalDateTime.now())
        .build();
    List<PayupInfo> payupInfos = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      LocalDateTime startDateTime = LocalDateTime.of(2023, 6, 4, 13, 0, 0)
          .plusDays(i);
      LocalDateTime endDateTime = startDateTime.plusHours(1L)
          .plusMinutes(30L);
      String nickname = "TestUser" + i;
      Integer paymentAmount = 10000;
      Integer payupAmount = PayupCalculator.calculateCharge(paymentAmount, PayupCalculator.DEFAULT_CHARGE);
      PayupStatus complete = PayupStatus.COMPLETE;
      PayupInfo payupInfo = new PayupInfo(startDateTime, endDateTime, nickname, paymentAmount, payupAmount, complete);
      payupInfos.add(payupInfo);
    }
    MentorPayupResult payupResult = MentorPayupResult.of(payupInfos);
    given(payupRepository.findAllByMonthRange(any(DateTimeRange.class), anyLong())).willReturn(payupInfos);
    //when
    MentorPayupResult mentorPayupResult = mentorService.getMentorPayupResult(monthRange, 1L);
    //then
    assertThat(mentorPayupResult).isEqualTo(payupResult);
  }
}