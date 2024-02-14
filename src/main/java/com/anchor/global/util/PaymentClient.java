package com.anchor.global.util;


import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.global.exception.AnchorException;
import com.anchor.global.exception.type.api.HttpResponseNotFoundException;
import com.anchor.global.exception.type.api.HttpStatus5xxException;
import com.anchor.global.exception.type.api.InvalidAccessTokenException;
import com.anchor.global.exception.type.api.InvalidImpUidException;
import com.anchor.global.exception.type.api.OtherHttpStatus4xxException;
import com.anchor.global.payment.portone.request.AccessTokenRequest;
import com.anchor.global.payment.portone.request.PortOneRequestUrl;
import com.anchor.global.payment.portone.request.PrepareRequestData;
import com.anchor.global.payment.portone.request.RequiredPaymentData;
import com.anchor.global.payment.portone.response.AccessTokenResult;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentRequestResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.payment.portone.response.PrepareResult;
import com.anchor.global.payment.portone.response.SinglePaymentResult;
import jakarta.servlet.http.HttpSession;
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

  private final JsonUtils jsonUtils;

  private final HttpSession session;

  @Value("${payment.imp-key}")
  private String impKey;

  @Value("${payment.imp-secret}")
  private String impSecret;

  public PaymentClient(@Qualifier("paymentRestClient") RestClient restClient, JsonUtils jsonUtils,
      HttpSession session) {
    this.restClient = restClient;
    this.jsonUtils = jsonUtils;
    this.session = session;
  }

  public Optional<PaymentResult> request(MentoringStatus status, RequiredPaymentData requiredPaymentData) {
    String accessToken = getAccessToken();
    return switch (status) {
      case CANCELLED -> cancelPayment(accessToken, requiredPaymentData);
      case WAITING -> getSinglePayment(accessToken, requiredPaymentData);
      default -> Optional.empty();
    };
  }

  public void preRegisterAmount(String userEmail, Integer amount) {
    String accessToken = getAccessToken();
    PrepareRequestData prepareRequestData = PrepareRequestData.of(userEmail, amount);
    PrepareResult prepareResult = prepareRegisterAmount(accessToken, prepareRequestData);
    session.setAttribute(SessionKeyType.MERCHANT_UID.getKey(), prepareResult.getMerchantUid());
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
    String tokenRequestToJson = jsonUtils.serializeObjectToJson(accessTokenRequest);
    PaymentRequestResult entity = restClient.post()
        .uri(PortOneRequestUrl.ACCESS_TOKEN_URL.getUrl())
        .contentType(MediaType.APPLICATION_JSON)
        .body(tokenRequestToJson)
        .exchange((request, response) -> checkStatus(response));

    return jsonUtils.convertValue(entity.getResponse(), AccessTokenResult.class);
  }

  private Optional<PaymentResult> getSinglePayment(String requestUrl, String accessToken) {
    PaymentRequestResult entity = restClient.get()
        .uri(requestUrl)
        .headers(header -> {
          header.add(HttpHeaders.AUTHORIZATION, accessToken);
          header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .exchange((request, response) -> checkStatus(response));

    return Optional.ofNullable(jsonUtils.convertValue(entity.getResponse(), SinglePaymentResult.class));
  }

  private Optional<PaymentResult> cancelPayment(String requestUrl, String accessToken, RequiredPaymentData data) {
    String requestBody = jsonUtils.serializeObjectToJson(data);
    PaymentRequestResult entity = restClient.post()
        .uri(requestUrl)
        .header(HttpHeaders.AUTHORIZATION, accessToken)
        .contentType(MediaType.APPLICATION_JSON)
        .body(requestBody)
        .exchange((request, response) -> checkStatus(response));

    return Optional.ofNullable(jsonUtils.convertValue(entity.getResponse(), PaymentCancelResult.class));
  }

  private PrepareResult prepareRegisterAmount(String accessToken, PrepareRequestData prepareRequestData) {
    String requestBody = jsonUtils.serializeObjectToJson(prepareRequestData);
    PaymentRequestResult entity = restClient.post()
        .uri(PortOneRequestUrl.PRE_REGISTER_URL.getUrl())
        .headers(header -> {
          header.add(HttpHeaders.AUTHORIZATION, accessToken);
          header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        })
        .body(requestBody)
        .exchange((request, response) -> checkStatus(response));
    return jsonUtils.convertValue(entity.getResponse(), PrepareResult.class);
  }

  private PaymentRequestResult checkStatus(ClientHttpResponse response) {
    try {
      HttpStatusCode statusCode = response.getStatusCode();
      if (statusCode.is5xxServerError()) {
        throw new HttpStatus5xxException();
      }
      PaymentRequestResult result = jsonUtils.deserializejsonToObject(response.getBody(), PaymentRequestResult.class);
      if (statusCode.is4xxClientError()) {
        exceptionByStatusCode(statusCode, result);
      }
      return result;
    } catch (IOException e) {
      throw new HttpResponseNotFoundException(e);
    }
  }

  private void exceptionByStatusCode(HttpStatusCode statusCode, PaymentRequestResult result) throws AnchorException {
    String message = result.getMessage();
    if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.UNAUTHORIZED.value()))) {
      throw new InvalidAccessTokenException(message);
    }
    if (statusCode.isSameCodeAs(HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value()))) {
      throw new InvalidImpUidException(message);
    }
    throw new OtherHttpStatus4xxException(message);
  }

}