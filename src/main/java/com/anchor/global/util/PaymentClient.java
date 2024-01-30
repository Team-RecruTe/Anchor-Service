package com.anchor.global.util;


import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.type.api.ApiClientException;
import com.anchor.global.exception.type.api.HttpClientException;
import com.anchor.global.exception.type.api.InvalidAccessTokenException;
import com.anchor.global.exception.type.api.InvalidImpUidException;
import com.anchor.global.exception.type.api.JsonDeserializationFailedException;
import com.anchor.global.exception.type.api.JsonSerializationFailedException;
import com.anchor.global.payment.portone.request.AccessTokenRequest;
import com.anchor.global.payment.portone.request.PortOneRequestUrl;
import com.anchor.global.payment.portone.request.RequiredPaymentData;
import com.anchor.global.payment.portone.response.AccessTokenResult;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentRequestResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.payment.portone.response.SinglePaymentResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
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
      PaymentRequestResult entity = restClient.post()
          .uri(PortOneRequestUrl.ACCESS_TOKEN_URL.getUrl())
          .contentType(MediaType.APPLICATION_JSON)
          .body(tokenRequestToJson)
          .exchange((request, response) -> checkStatus(response));

      return objectMapper.convertValue(entity.getResponse(), AccessTokenResult.class);
    } catch (JsonProcessingException e) {
      throw new JsonSerializationFailedException(e);
    }
  }

  private Optional<PaymentResult> getSinglePayment(String requestUrl, String accessToken) {
    PaymentRequestResult entity = restClient.get()
        .uri(requestUrl)
        .headers(header -> {
          header.add(HttpHeaders.AUTHORIZATION, accessToken);
          header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .exchange((request, response) -> checkStatus(response));

    return Optional.ofNullable(objectMapper.convertValue(entity.getResponse(), SinglePaymentResult.class));
  }

  private Optional<PaymentResult> cancelPayment(String requestUrl, String accessToken, RequiredPaymentData data) {
    try {
      String requestBody = objectMapper.writeValueAsString(data);
      PaymentRequestResult entity = restClient.post()
          .uri(requestUrl)
          .header(HttpHeaders.AUTHORIZATION, accessToken)
          .contentType(MediaType.APPLICATION_JSON)
          .body(requestBody)
          .exchange((request, response) -> checkStatus(response));

      return Optional.ofNullable(objectMapper.convertValue(entity.getResponse(), PaymentCancelResult.class));
    } catch (JsonProcessingException e) {
      throw new JsonSerializationFailedException(e);
    }
  }

  private PaymentRequestResult checkStatus(ClientHttpResponse response) {
    try {
      HttpStatusCode statusCode = response.getStatusCode();
      if (statusCode.is5xxServerError()) {
        throw new ApiClientException();
      }
      PaymentRequestResult result = objectMapper.readValue(response.getBody(), PaymentRequestResult.class);
      if (statusCode.is4xxClientError()) {
        throw exceptionByStatusCode(statusCode, result);
      }
      return result;
    } catch (JsonProcessingException e) {
      throw new JsonDeserializationFailedException(e);
    } catch (IOException e) {
      throw new HttpClientException(e);
    }
  }

  private ServiceException exceptionByStatusCode(HttpStatusCode statusCode, PaymentRequestResult result) {
    String message = result.getMessage();
    if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()))) {
      return new InvalidAccessTokenException(message);
    }
    if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))) {
      return new InvalidImpUidException(message);
    }
    return new ApiClientException(message);
  }

}