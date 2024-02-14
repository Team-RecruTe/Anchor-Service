package com.anchor.global.util;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.exception.AnchorException;
import com.anchor.global.exception.type.api.HttpResponseNotFoundException;
import com.anchor.global.exception.type.api.HttpStatus5xxException;
import com.anchor.global.exception.type.api.InvalidParamException;
import com.anchor.global.nhpay.request.PayupRequestHeader;
import com.anchor.global.nhpay.request.PayupRequestHeaderCreator;
import com.anchor.global.nhpay.request.RequiredAccountHolderData;
import com.anchor.global.nhpay.request.RequiredDepositData;
import com.anchor.global.nhpay.response.AccountHolderResult;
import com.anchor.global.nhpay.response.DepositResult;
import com.anchor.global.nhpay.response.PayupResult;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class PayupClient {

  private final RestClient restClient;
  private final JsonUtils jsonUtils;
  private final PayupRequestHeaderCreator headerCreator;

  public PayupClient(@Qualifier("payUpRestClient") RestClient restClient,
      JsonUtils jsonUtils, PayupRequestHeaderCreator headerCreator) {
    this.restClient = restClient;
    this.jsonUtils = jsonUtils;
    this.headerCreator = headerCreator;
  }

  @Retryable(retryFor = {SocketTimeoutException.class, ResourceAccessException.class},
      maxAttempts = 2, backoff = @Backoff(delay = 500))
  public boolean validateAccountHolder(Mentor mentor, Set<Mentor> failMentor) {
    String requestUrl = headerCreator.createAccountRequestUrl(mentor);
    PayupRequestHeader accountHolderHeader = headerCreator.createAccountRequestHeader(mentor);
    try {
      RequiredAccountHolderData requiredData = RequiredAccountHolderData.of(accountHolderHeader, mentor);
      String body = jsonUtils.serializeObjectToJson(requiredData);
      AccountHolderResult result = request(requestUrl, body, AccountHolderResult.class);
      return validateResponse(result);
    } catch (AnchorException e) {
      failMentor.add(mentor);
      return false;
    }
  }

  @Retryable(retryFor = {SocketTimeoutException.class, ResourceAccessException.class},
      maxAttempts = 2, backoff = @Backoff(delay = 500))
  public void requestPayup(Mentor mentor, Integer totalAmount, Set<Mentor> failMentor) {
    String requestUrl = headerCreator.createDepositRequestUrl(mentor);
    PayupRequestHeader depositRequestHeader = headerCreator.createDepositRequestHeader(mentor);
    try {
      RequiredDepositData requiredData = RequiredDepositData.of(depositRequestHeader, mentor, totalAmount);
      String body = jsonUtils.serializeObjectToJson(requiredData);
      DepositResult result = request(requestUrl, body, DepositResult.class);
      validateResponse(result);
    } catch (AnchorException e) {
      failMentor.add(mentor);
    }
  }

  private <T extends PayupResult> T request(String requestUrl, String requestBody, Class<T> clazz) {
    return restClient.post()
        .uri(requestUrl)
        .accept(APPLICATION_JSON)
        .contentType(APPLICATION_JSON)
        .body(requestBody)
        .exchange((request, response) -> checkStatusCode(response, clazz));
  }

  private <T extends PayupResult> T checkStatusCode(ClientHttpResponse response, Class<T> clazz) {
    try {
      HttpStatusCode statusCode = response.getStatusCode();
      if (statusCode.is4xxClientError() || statusCode.is5xxServerError()) {
        throw new HttpStatus5xxException();
      }
      return jsonUtils.deserializejsonToObject(response.getBody(), clazz);
    } catch (IOException e) {
      throw new HttpResponseNotFoundException(e);
    }
  }

  private boolean validateResponse(PayupResult payupResult) {
    if (payupResult.validateResponseCode()) {
      return true;
    }
    throw new InvalidParamException(payupResult.getMessage());
  }

}