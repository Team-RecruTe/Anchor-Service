package com.anchor.global.util;


import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.global.payment.portone.request.AccessTokenRequest;
import com.anchor.global.payment.portone.request.RequiredPaymentData;
import com.anchor.global.payment.portone.response.AccessTokenResult;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentRequestResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.payment.portone.response.SinglePaymentResult;
import com.anchor.global.portone.request.PortOneRequestUrl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

  private final RestClient restClient;

  private final ObjectMapper objectMapper;

  @Value("${payment.imp-key}")
  private String impKey;

  @Value("${payment.imp-secret}")
  private String impSecret;

  public PaymentClient(@Qualifier("paymentRestClient") RestClient restClient, ObjectMapper objectMapper) {
    this.restClient = restClient;
    this.objectMapper = objectMapper;
  }

  public Optional<PaymentResult> request(MentoringStatus status, RequiredPaymentData requiredPaymentData) {
    String accessToken = getAccessToken();
    return switch (status) {
      case CANCELLED -> cancelPayment(accessToken, requiredPaymentData);
      case WAITING -> getSinglePayment(accessToken, requiredPaymentData);
      default -> Optional.empty();
    };
  }

  private Optional<PaymentResult> getSinglePayment(String accessToken, RequiredPaymentData paymentResultInfo) {
    String requestUrl = createPaymentDataRequestUrl(paymentResultInfo.getImpUid());
    return getSinglePayment(requestUrl, accessToken);
  }

  private Optional<PaymentResult> cancelPayment(String accessToken, RequiredPaymentData requiredPaymentCancelData) {
    return cancelPayment(PortOneRequestUrl.CANCEL_PAYMENT_URL.getUrl(), accessToken, requiredPaymentCancelData);
  }

  private String getAccessToken() {
    AccessTokenRequest accessTokenRequest = new AccessTokenRequest(impKey, impSecret);
    return getToken(accessTokenRequest).getAccessToken();
  }

  private String createPaymentDataRequestUrl(String impUid) {
    return PortOneRequestUrl.CREATE_PAYMENT_URL.getUrl() + impUid;
  }

  private AccessTokenResult getToken(AccessTokenRequest accessTokenRequest) {
    try {
      String tokenRequestToJson = objectMapper.writeValueAsString(accessTokenRequest);
      ResponseEntity<PaymentRequestResult> entity = restClient.post()
          .uri(PortOneRequestUrl.ACCESS_TOKEN_URL.getUrl())
          .contentType(MediaType.APPLICATION_JSON)
          .body(tokenRequestToJson)
          .retrieve()
          .toEntity(PaymentRequestResult.class);

      Object response = getPaymentRequestDetail(entity);
      return objectMapper.convertValue(response, AccessTokenResult.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }
  }

  private Optional<PaymentResult> getSinglePayment(String requestUrl, String accessToken) {
    ResponseEntity<PaymentRequestResult> entity = restClient.get()
        .uri(requestUrl)
        .headers(header -> {
          header.add(HttpHeaders.AUTHORIZATION, accessToken);
          header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .retrieve()
        .toEntity(PaymentRequestResult.class);

    Object response = getPaymentRequestDetail(entity);
    return Optional.ofNullable(objectMapper.convertValue(response, SinglePaymentResult.class));
  }

  private Optional<PaymentResult> cancelPayment(String requestUrl, String accessToken, RequiredPaymentData data) {
    try {
      String requestBody = objectMapper.writeValueAsString(data);
      ResponseEntity<PaymentRequestResult> entity = restClient.post()
          .uri(requestUrl)
          .header(HttpHeaders.AUTHORIZATION, accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .body(requestBody)
          .retrieve()
          .toEntity(PaymentRequestResult.class);

      Object response = getPaymentRequestDetail(entity);
      return Optional.ofNullable(objectMapper.convertValue(response, PaymentCancelResult.class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }
  }

  private Object getPaymentRequestDetail(ResponseEntity<PaymentRequestResult> result) {
    try {
      PaymentRequestResult paymentCancelResult = result.getBody();
      return paymentCancelResult.getResponse();
    } catch (NullPointerException e) {
      throw new RuntimeException("요청이 제대로 처리되지 않았습니다.");
    }
  }
}