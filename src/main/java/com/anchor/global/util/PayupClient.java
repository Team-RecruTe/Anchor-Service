package com.anchor.global.util;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.type.api.ApiClientException;
import com.anchor.global.exception.type.api.HttpClientException;
import com.anchor.global.exception.type.api.JsonDeserializationFailedException;
import com.anchor.global.nhpay.request.PayupRequestHeader;
import com.anchor.global.nhpay.request.PayupRequestHeaderCreator;
import com.anchor.global.nhpay.request.RequiredAccountHolderData;
import com.anchor.global.nhpay.request.RequiredDepositData;
import com.anchor.global.nhpay.response.AccountHolderResult;
import com.anchor.global.nhpay.response.DepositResult;
import com.anchor.global.nhpay.response.PayupResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class PayupClient {

  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  private final PayupRequestHeaderCreator headerCreator;

  public PayupClient(@Qualifier("payUpRestClient") RestClient restClient, ObjectMapper objectMapper,
      PayupRequestHeaderCreator headerCreator) {
    this.restClient = restClient;
    this.objectMapper = objectMapper;
    this.headerCreator = headerCreator;
  }

  public boolean validateAccountHolder(Mentor mentor, Set<Mentor> failMentor) {
    String requestUrl = headerCreator.createAccountRequestUrl(mentor);
    PayupRequestHeader accountHolderHeader = headerCreator.createAccountRequestHeader(mentor);
    try {
      RequiredAccountHolderData requiredData = RequiredAccountHolderData.of(accountHolderHeader, mentor);
      String body = objectMapper.writeValueAsString(requiredData);
      AccountHolderResult result = request(requestUrl, body, AccountHolderResult.class);
      return validateResponse(result);
    } catch (JsonProcessingException | ServiceException e) {
      failMentor.add(mentor);
      return false;
    }
  }

  public void requestPayup(Mentor mentor, Integer totalAmount, Set<Mentor> failMentor) {
    String requestUrl = headerCreator.createDepositRequestUrl(mentor);
    PayupRequestHeader depositRequestHeader = headerCreator.createDepositRequestHeader(mentor);
    try {
      RequiredDepositData requiredData = RequiredDepositData.of(depositRequestHeader, mentor, totalAmount);
      String body = objectMapper.writeValueAsString(requiredData);
      DepositResult result = request(requestUrl, body, DepositResult.class);
      validateResponse(result);
    } catch (JsonProcessingException | ServiceException e) {
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
        throw new ApiClientException();
      }
      return objectMapper.readValue(response.getBody(), clazz);
    } catch (JsonProcessingException e) {
      throw new JsonDeserializationFailedException(e);
    } catch (IOException e) {
      throw new HttpClientException(e);
    }
  }

  private boolean validateResponse(PayupResult payupResult) {
    if (payupResult.validateResponseCode()) {
      return true;
    }
    throw new ApiClientException(payupResult.getMessage());
  }

}