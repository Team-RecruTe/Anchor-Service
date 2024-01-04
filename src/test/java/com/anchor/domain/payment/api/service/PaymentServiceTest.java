package com.anchor.domain.payment.api.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.controller.request.TokenRequest;
import com.anchor.domain.payment.api.service.response.SinglePaymentData;
import com.anchor.domain.payment.api.service.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.domain.payment.api.service.response.TokenData;
import com.anchor.domain.payment.api.service.response.TokenData.TokenDataDetail;
import com.anchor.global.util.ExternalApiClient;
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
  @InjectMocks
  private PaymentService paymentService;

  void fieldPropertiesValueInit() {
    ReflectionTestUtils.setField(paymentService, "impKey", "testImpKey");
    ReflectionTestUtils.setField(paymentService, "impSecret", "testImpSecret");
  }

  void wrongFieldPropertiesValueInit() {
    ReflectionTestUtils.setField(paymentService, "impKey", "wrongImpKey");
    ReflectionTestUtils.setField(paymentService, "impSecret", "wrongImpSecret");
  }

  @Test
  @DisplayName("결제 사후검증 요청이 들어오면 API서버와 결제금액을 검증한다.")
  void validatePaymentResultTest() {

    //given
    fieldPropertiesValueInit();

    PaymentResultInfo paymentResultInfo = PaymentResultInfo.builder()
        .amount(100)
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .build();
    String mockAccessToken = "testAccessToken";

    TokenDataDetail mockTokenDataDetail = TokenDataDetail.builder()
        .accessToken(mockAccessToken)
        .now(123456L)
        .expiredAt(1234566L)
        .build();
    TokenData mockTokenData = TokenData.builder()
        .code(0)
        .message(null)
        .response(mockTokenDataDetail)
        .build();

    ResponseEntity<TokenData> mockTokenResponseEntity = new ResponseEntity<>(mockTokenData, HttpStatus.OK);

    PaymentDataDetail mockPaymentDataDetail = PaymentDataDetail.builder()
        .amount(100)
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .build();

    SinglePaymentData mockSinglePaymentData = SinglePaymentData.builder()
        .code(0)
        .message(null)
        .response(mockPaymentDataDetail)
        .build();

    ResponseEntity<SinglePaymentData> mockPaymentDataEntity = new ResponseEntity<>(mockSinglePaymentData,
        HttpStatus.OK);

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

    PaymentResultInfo paymentResultInfo = PaymentResultInfo.builder()
        .amount(100)
        .impUid("testImpUid")
        .merchantUid("testMerchantUid")
        .build();

    TokenData mockTokenData = TokenData.builder()
        .code(-1)
        .message("인증에 실패하였습니다. API키와 secret을 확인하세요. wrongImpKey, wrongImpSecret")
        .response(null)
        .build();

    ResponseEntity<TokenData> mockTokenResponseEntity = new ResponseEntity<>(mockTokenData, HttpStatus.UNAUTHORIZED);

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

    PaymentResultInfo paymentResultInfo = PaymentResultInfo.builder()
        .amount(100)
        .impUid("wrongImpUid")
        .merchantUid("testMerchantUid")
        .build();
    String mockAccessToken = "testAccessToken";

    TokenDataDetail mockTokenDataDetail = TokenDataDetail.builder()
        .accessToken(mockAccessToken)
        .now(123456L)
        .expiredAt(1234566L)
        .build();
    TokenData mockTokenData = TokenData.builder()
        .code(0)
        .message(null)
        .response(mockTokenDataDetail)
        .build();

    ResponseEntity<TokenData> mockTokenResponseEntity = new ResponseEntity<>(mockTokenData, HttpStatus.OK);

    SinglePaymentData mockSinglePaymentData = SinglePaymentData.builder()
        .code(-1)
        .message("존재하지 않는 결제정보입니다.")
        .response(null)
        .build();

    ResponseEntity<SinglePaymentData> mockPaymentDataEntity = new ResponseEntity<>(mockSinglePaymentData,
        HttpStatus.NOT_FOUND);

    given(apiClient.getTokenDataEntity(any(TokenRequest.class), anyString())).willReturn(mockTokenResponseEntity);
    given(apiClient.getSinglePaymentDataEntity(anyString(), anyString())).willReturn(mockPaymentDataEntity);

    //when then
    assertThatThrownBy(() -> paymentService.validatePaymentResult(paymentResultInfo))
        .isInstanceOf(RuntimeException.class)
        .hasMessage("존재하지 않는 결제정보입니다.");
  }
}