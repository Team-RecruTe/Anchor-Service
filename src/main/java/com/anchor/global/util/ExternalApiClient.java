package com.anchor.global.util;


import com.anchor.global.portone.request.ApiPaymentCancelData;
import com.anchor.global.portone.request.TokenRequest;
import com.anchor.global.portone.response.PaymentCancelData;
import com.anchor.global.portone.response.SinglePaymentData;
import com.anchor.global.portone.response.TokenData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ExternalApiClient {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;

  public ResponseEntity<TokenData> getTokenDataEntity(TokenRequest tokenRequest, String accessTokenUrl) {
    try {

      String tokenRequestToJson = objectMapper.writeValueAsString(tokenRequest);

      return restClient.post()
          .uri(accessTokenUrl)
          .contentType(MediaType.APPLICATION_JSON)
          .body(tokenRequestToJson)
          .retrieve()
          .toEntity(TokenData.class);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }

  }

  public ResponseEntity<SinglePaymentData> getSinglePaymentDataEntity(String paymentUrl, String accessToken) {

    return restClient.get()
        .uri(paymentUrl)
        .headers(header -> {
          header.add(HttpHeaders.AUTHORIZATION, accessToken);
          header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .retrieve()
        .toEntity(SinglePaymentData.class);
  }

  public ResponseEntity<PaymentCancelData> getPaymentCancelData
      (ApiPaymentCancelData cancelData, String accessToken, String cancelUrl) {
    try {

      String requestBody = objectMapper.writeValueAsString(cancelData);

      return restClient.post()
          .uri(cancelUrl)
          .header(HttpHeaders.AUTHORIZATION, accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .body(requestBody)
          .retrieve()
          .toEntity(PaymentCancelData.class);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }
  }
}