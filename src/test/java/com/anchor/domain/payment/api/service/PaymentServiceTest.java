package com.anchor.domain.payment.api.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.controller.request.RequiredPaymentInfo;
import com.anchor.domain.payment.api.controller.request.TokenRequest;
import com.anchor.domain.payment.api.service.response.PaidMentoringInfo;
import com.anchor.domain.payment.api.service.response.SinglePaymentData;
import com.anchor.domain.payment.api.service.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.domain.payment.api.service.response.TokenData;
import com.anchor.domain.payment.api.service.response.TokenData.TokenDataDetail;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.ExternalApiClient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock
  private ExternalApiClient apiClient;
  @Mock
  private PaymentRepository paymentRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MentoringApplicationRepository mentoringApplicationRepository;
  @InjectMocks
  private PaymentService paymentService;

  private User user;
  private SessionUser sessionUser;

  void fieldPropertiesValueInit() {
    ReflectionTestUtils.setField(paymentService, "impKey", "testImpKey");
    ReflectionTestUtils.setField(paymentService, "impSecret", "testImpSecret");
  }

  void wrongFieldPropertiesValueInit() {
    ReflectionTestUtils.setField(paymentService, "impKey", "wrongImpKey");
    ReflectionTestUtils.setField(paymentService, "impSecret", "wrongImpSecret");
  }

  void sessionUserInit() {
    user = User.builder()
        .id(1L)
        .email("test@test.com")
        .nickname("testName")
        .build();

    sessionUser = new SessionUser(user);
  }

  @Test
  @DisplayName("결제 사후검증 요청이 들어오면 API서버와 결제금액을 검증한다.")
  void validatePaymentResultTest() {

    //given
    fieldPropertiesValueInit();

    PaymentResultInfo paymentResultInfo = createPaymentResultInfo();

    ResponseEntity<TokenData> mockTokenResponseEntity = new ResponseEntity<>(createTokenData(), HttpStatus.OK);

    ResponseEntity<SinglePaymentData> mockPaymentDataEntity =
        new ResponseEntity<>(createSinglePaymentData(), HttpStatus.OK);

    given(apiClient.getTokenDataEntity(any(TokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);

    given(apiClient.getSinglePaymentDataEntity(anyString(), anyString())).willReturn(mockPaymentDataEntity);

    //when
    String result = paymentService.validatePaymentResult(paymentResultInfo);

    //then
    assertThat(result).isEqualTo("success");
  }


  @Test
  @DisplayName("AccessToken 조회시 잘못된 API Key가 입력되면 예외를 발생시킨다.")
  void inputWrongApiKeyTest() {

    //given
    wrongFieldPropertiesValueInit();

    PaymentResultInfo paymentResultInfo = createPaymentResultInfo();

    ResponseEntity<TokenData> mockTokenResponseEntity =
        new ResponseEntity<>(createWrongTokenData(), HttpStatus.UNAUTHORIZED);

    given(apiClient.getTokenDataEntity(any(TokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);

    //when then
    assertThatThrownBy(() -> paymentService.validatePaymentResult(paymentResultInfo)).isInstanceOf(
            RuntimeException.class)
        .hasMessage("인증에 실패하였습니다. API키와 secret을 확인하세요. wrongImpKey, wrongImpSecret");
  }


  @Test
  @DisplayName("잘못된 결제번호를 입력하면 예외를 발생시킨다.")
  void inputWrongImpUidTest() {
    //given
    fieldPropertiesValueInit();

    PaymentResultInfo paymentResultInfo = createWrongPaymentResultInfo();

    ResponseEntity<TokenData> mockTokenResponseEntity = new ResponseEntity<>(createTokenData(), HttpStatus.OK);

    ResponseEntity<SinglePaymentData> mockPaymentDataEntity =
        new ResponseEntity<>(createWrongPaymentData(), HttpStatus.NOT_FOUND);

    given(apiClient.getTokenDataEntity(any(TokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);

    given(apiClient.getSinglePaymentDataEntity(anyString(), anyString())).willReturn(mockPaymentDataEntity);

    //when then
    assertThatThrownBy(() -> paymentService.validatePaymentResult(paymentResultInfo))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 결제정보입니다.");
  }

  @Test
  @DisplayName("결제정보와 회원정보가 입력되면 결제정보를 DB에 저장한다.")
  void savePaymentTest() {
    //given
    sessionUserInit();

    RequiredPaymentInfo requiredPaymentInfo = createRequiredPaymentInfo();

    MentoringApplication mentoringApplication = createMentoringApplication();

    Payment payment = Payment.builder()
        .requiredPaymentInfo(requiredPaymentInfo)
        .build();

    PaidMentoringInfo paidMentoringInfo = new PaidMentoringInfo(mentoringApplication, payment);

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    given(mentoringApplicationRepository.findAppliedMentoringByTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willReturn(Optional.of(mentoringApplication));

    given(paymentRepository.save(any(Payment.class))).willReturn(payment);

    //when
    PaidMentoringInfo result = paymentService.savePayment(requiredPaymentInfo, sessionUser);

    //then
    assertThat(result).isEqualTo(paidMentoringInfo);
  }

  @Test
  @DisplayName("로그인 회원정보로 회원정보를 조회할 수 없다면 예외를 발생시킨다.")
  void inputWrongSessionUserInfoTest() {
    //given
    sessionUserInit();

    RequiredPaymentInfo requiredPaymentInfo = createRequiredPaymentInfo();

    given(userRepository.findByEmail(anyString()))
        .willThrow(new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));

    //when

    PaidMentoringInfo result = paymentService.savePayment(requiredPaymentInfo, sessionUser);

    //then
    assertThat(result).isNull();
    verify(mentoringApplicationRepository, never()).findAppliedMentoringByTimeAndUserId(any(LocalDateTime.class),
        any(LocalDateTime.class), anyLong());
    verify(paymentRepository, never()).save(any());
  }

  @Test
  @DisplayName("멘토링 신청시간이 잘못 입력되면 멘토링신청이력 조회시 예외를 발생시킨다.")
  void inputWrongTimeOrUserInfoTest() {
    //given
    sessionUserInit();

    RequiredPaymentInfo requiredPaymentInfo = createRequiredPaymentInfo();

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    given(mentoringApplicationRepository.findAppliedMentoringByTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willThrow(new NoSuchElementException("조건에 부합하는 멘토링 신청이력이 존재하지 않습니다."));

    //when
    PaidMentoringInfo result = paymentService.savePayment(requiredPaymentInfo, sessionUser);

    //then
    assertThat(result).isNull();
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  @Test
  @DisplayName("멘토링 신청내역에 이미 결제내역이 존재한다면 예외를 발생시킨다.")
  void alreadyExistPaymentDataTest() {
    //given
    sessionUserInit();

    RequiredPaymentInfo requiredPaymentInfo = createRequiredPaymentInfo();

    MentoringApplication mentoringApplicationExistPayment = createMentoringApplicationExistPayment(requiredPaymentInfo);

    given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

    given(mentoringApplicationRepository.findAppliedMentoringByTimeAndUserId
        (any(LocalDateTime.class), any(LocalDateTime.class), anyLong()))
        .willReturn(Optional.of(mentoringApplicationExistPayment));

    //when
    PaidMentoringInfo result = paymentService.savePayment(requiredPaymentInfo, sessionUser);

    //then
    assertThat(result).isNull();
    verify(paymentRepository, never()).save(any(Payment.class));
  }

  private PaymentResultInfo createPaymentResultInfo() {
    return PaymentResultInfo.builder()
        .amount(100)
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .build();
  }

  private PaymentResultInfo createWrongPaymentResultInfo() {
    return PaymentResultInfo.builder()
        .amount(100)
        .impUid("wrongImpUid")
        .merchantUid("testMerchantUid")
        .build();
  }

  private TokenData createTokenData() {
    String mockAccessToken = "testAccessToken";

    TokenDataDetail mockTokenDataDetail = TokenDataDetail.builder()
        .accessToken(mockAccessToken)
        .now(123456L)
        .expiredAt(1234566L)
        .build();

    return TokenData.builder()
        .code(0)
        .message(null)
        .response(mockTokenDataDetail)
        .build();
  }

  private TokenData createWrongTokenData() {
    return TokenData.builder()
        .code(-1)
        .message("인증에 실패하였습니다. API키와 secret을 확인하세요. wrongImpKey, wrongImpSecret")
        .response(null)
        .build();
  }

  private SinglePaymentData createSinglePaymentData() {
    PaymentDataDetail mockPaymentDataDetail = PaymentDataDetail.builder()
        .amount(100)
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .build();

    return SinglePaymentData.builder()
        .code(0)
        .message(null)
        .response(mockPaymentDataDetail)
        .build();
  }

  private SinglePaymentData createWrongPaymentData() {
    return SinglePaymentData.builder()
        .code(-1)
        .message("존재하지 않는 결제정보입니다.")
        .response(null)
        .build();
  }

  private RequiredPaymentInfo createRequiredPaymentInfo() {
    return RequiredPaymentInfo.builder()
        .startDateTime(LocalDateTime.of(2024, 1, 3, 13, 0))
        .endDateTime(LocalDateTime.of(2024, 1, 3, 14, 0))
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .amount(100)
        .build();
  }

  private MentoringApplication createMentoringApplication() {
    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title("testTitle")
        .mentor(Mentor.builder()
            .build())
        .build();

    return MentoringApplication.builder()
        .user(user)
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .build();
  }

  private MentoringApplication createMentoringApplicationExistPayment(RequiredPaymentInfo requiredPaymentInfo) {
    MentoringApplicationTime applicationTime = MentoringApplicationTime.builder()
        .date(LocalDate.of(2024, 1, 3))
        .time(LocalTime.of(13, 0))
        .build();

    Mentoring mentoring = Mentoring.builder()
        .title("testTitle")
        .mentor(Mentor.builder()
            .build())
        .build();

    MentoringApplication mentoringApplication = MentoringApplication.builder()
        .user(user)
        .mentoringApplicationTime(applicationTime)
        .mentoring(mentoring)
        .build();

    Payment payment = Payment.builder()
        .requiredPaymentInfo(requiredPaymentInfo)
        .mentoringApplication(mentoringApplication)
        .build();

    return mentoringApplication;
  }
}