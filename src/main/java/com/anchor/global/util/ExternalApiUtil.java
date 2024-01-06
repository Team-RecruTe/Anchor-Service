package com.anchor.global.util;


import com.anchor.domain.payment.domain.Payment;
import com.anchor.global.portone.request.ApiPaymentCancelData;
import com.anchor.global.portone.request.TokenRequest;
import com.anchor.global.portone.response.PaymentCancelData;
import com.anchor.global.portone.response.SinglePaymentData;
import com.anchor.global.portone.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.global.portone.response.TokenData;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExternalApiUtil {

  private static final String ACCESS_TOKEN_URL = "https://api.iamport.kr/users/getToken";
  private static final String CANCEL_PAYMENT_URL = "https://api.iamport.kr/payments/cancel";

  private final ExternalApiClient apiClient;

  @Value("${payment.imp-key}")
  private String impKey;
  @Value("${payment.imp-secret}")
  private String impSecret;

  public PaymentDataDetail getPaymentDataDetail(
      com.anchor.domain.payment.api.controller.request.PaymentResultInfo paymentResultInfo) {
    String accessToken = getAccessToken();

    String paymentDataRequestUrl = createPaymentDataRequestUrl(paymentResultInfo.getImpUid());

    ResponseEntity<SinglePaymentData> singlePaymentDataEntity = apiClient.getSinglePaymentDataEntity(
        paymentDataRequestUrl, accessToken);

    SinglePaymentData paymentData = Objects.requireNonNullElse(singlePaymentDataEntity.getBody(), null);

    if (paymentData.statusCheck()) {

      return paymentData.getResponse();

    } else {
      throw new RuntimeException(paymentData.getMessage());
    }

  }


  public boolean paymentCancel(Payment payment) {
    String accessToken = getAccessToken();

    ApiPaymentCancelData apiPaymentCancelData = new ApiPaymentCancelData(payment);

    ResponseEntity<PaymentCancelData> paymentCancelDataEntity = apiClient.getPaymentCancelData(apiPaymentCancelData,
        accessToken, CANCEL_PAYMENT_URL);

    PaymentCancelData paymentCancelData = Objects.requireNonNullElse(paymentCancelDataEntity.getBody(), null);

    if (paymentCancelData.statusCheck()) {

      return true;
    } else {

      throw new RuntimeException(paymentCancelData.getMessage());
    }
  }

  private String getAccessToken() {
    TokenRequest tokenRequest = new TokenRequest(impKey, impSecret);

    ResponseEntity<TokenData> tokenDataEntity = apiClient.getTokenDataEntity(tokenRequest, ACCESS_TOKEN_URL);

    TokenData tokenData = Objects.requireNonNullElse(tokenDataEntity.getBody(), null);

    if (tokenData.statusCheck()) {

      return tokenData.getResponse()
          .getAccessToken();

    } else {
      throw new RuntimeException(tokenData.getMessage());
    }
  }

  private String createPaymentDataRequestUrl(String impUid) {
    return "https://api.iamport.kr/payments/" + impUid;
  }
}