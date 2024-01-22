package com.anchor.domain.payment.api.service;


import com.anchor.global.util.PaymentUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @InjectMocks
  private PaymentUtils apiUtil;

  private PaymentService paymentService;

  void fieldPropertiesValueInit() {
    ReflectionTestUtils.setField(apiUtil, "impKey", "testImpKey");
    ReflectionTestUtils.setField(apiUtil, "impSecret", "testImpSecret");
  }

  void wrongFieldPropertiesValueInit() {
    ReflectionTestUtils.setField(apiUtil, "impKey", "wrongImpKey");
    ReflectionTestUtils.setField(apiUtil, "impSecret", "wrongImpSecret");
  }

  @BeforeEach
  void setPaymentService() {
    paymentService = new PaymentService(apiUtil);
  }

//  @Test
//  @DisplayName("결제 사후검증 요청이 들어오면 API서버와 결제금액을 검증한다.")
//  void validatePaymentResultTest() {
//
//    //given
//    fieldPropertiesValueInit();
//
//    PaymentResultInfo paymentResultInfo = createPaymentResultInfo();
//
//    ResponseEntity<AccessTokenResult> mockTokenResponseEntity = new ResponseEntity<>(createTokenData(), HttpStatus.OK);
//
//    ResponseEntity<SinglePaymentResult> mockPaymentDataEntity =
//        new ResponseEntity<>(createSinglePaymentData(), HttpStatus.OK);
//
//    given(apiClient.getToken(any(AccessTokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);
//    given(apiClient.getSinglePayment(anyString(), anyString())).willReturn(mockPaymentDataEntity);
//
//    //when
//    String result = paymentService.validatePaymentResult(paymentResultInfo);
//
//    //then
//    assertThat(result).isEqualTo("success");
//  }
//
//
//  @Test
//  @DisplayName("AccessToken 조회시 잘못된 API Key가 입력되면 예외를 발생시킨다.")
//  void inputWrongApiKeyTest() {
//
//    //given
//    wrongFieldPropertiesValueInit();
//
//    PaymentResultInfo paymentResultInfo = createPaymentResultInfo();
//
//    ResponseEntity<AccessTokenResult> mockTokenResponseEntity =
//        new ResponseEntity<>(createWrongTokenData(), HttpStatus.UNAUTHORIZED);
//
//    given(apiClient.getToken(any(AccessTokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);
//
//    //when then
//    assertThatThrownBy(() -> paymentService.validatePaymentResult(paymentResultInfo)).isInstanceOf(
//            RuntimeException.class)
//        .hasMessage(
//            "인증에 실패하였습니다. API키와 secret을 확인하세요. wrongImpKey, wrongImpSecret");
//  }
//
//
//  @Test
//  @DisplayName("잘못된 결제번호를 입력하면 예외를 발생시킨다.")
//  void inputWrongImpUidTest() {
//    //given
//    fieldPropertiesValueInit();
//
//    PaymentResultInfo paymentResultInfo = createWrongPaymentResultInfo();
//
//    ResponseEntity<AccessTokenResult> mockTokenResponseEntity = new ResponseEntity<>(createTokenData(), HttpStatus.OK);
//
//    ResponseEntity<SinglePaymentResult> mockPaymentDataEntity =
//        new ResponseEntity<>(createWrongPaymentData(), HttpStatus.NOT_FOUND);
//
//    given(apiClient.getToken(any(AccessTokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);
//
//    given(apiClient.getSinglePayment(anyString(), anyString())).willReturn(mockPaymentDataEntity);
//
//    //when then
//    assertThatThrownBy(() -> paymentService.validatePaymentResult(paymentResultInfo))
//        .isInstanceOf(RuntimeException.class)
//        .hasMessage("존재하지 않는 결제정보입니다.");
//  }
//
//  private PaymentResultInfo createPaymentResultInfo() {
//    return PaymentResultInfo.builder()
//        .amount(100)
//        .impUid("testImpUid")
//        .merchantUid("testMerchantUid")
//        .build();
//  }
//
//  private PaymentResultInfo createWrongPaymentResultInfo() {
//    return PaymentResultInfo.builder()
//        .amount(100)
//        .impUid("wrongImpUid")
//        .merchantUid("testMerchantUid")
//        .build();
//  }
//
//  private AccessTokenResult createTokenData() {
//    String mockAccessToken = "testAccessToken";
//
//    TokenDataDetail mockTokenDataDetail = TokenDataDetail.builder()
//        .accessToken(mockAccessToken)
//        .now(123456L)
//        .expiredAt(1234566L)
//        .build();
//
//    return AccessTokenResult.builder()
//        .code(0)
//        .message(null)
//        .response(mockTokenDataDetail)
//        .build();
//  }
//
//  private AccessTokenResult createWrongTokenData() {
//    return AccessTokenResult.builder()
//        .code(-1)
//        .message("인증에 실패하였습니다. API키와 secret을 확인하세요. wrongImpKey, wrongImpSecret")
//        .response(null)
//        .build();
//  }
//
//  private SinglePaymentResult createSinglePaymentData() {
//    PaymentDataDetail mockPaymentDataDetail = PaymentDataDetail.builder()
//        .amount(100)
//        .impUid("testImpUid")
//        .merchantUid("testMerchantUid")
//        .build();
//
//    return SinglePaymentResult.builder()
//        .code(0)
//        .message(null)
//        .response(mockPaymentDataDetail)
//        .build();
//  }
//
//  private SinglePaymentResult createWrongPaymentData() {
//    return SinglePaymentResult.builder()
//        .code(-1)
//        .message("존재하지 않는 결제정보입니다.")
//        .response(null)
//        .build();
//  }
}