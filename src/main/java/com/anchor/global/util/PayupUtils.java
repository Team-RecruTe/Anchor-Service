package com.anchor.global.util;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.nhpay.request.NHRequestUrl;
import com.anchor.global.nhpay.request.PayupRequestHeader;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;

@Slf4j
@Component
public class PayupUtils {


  private final RestClient restClient;
  private final ObjectMapper objectMapper;
  @Value("${payup.access-token}")
  private String accessToken;
  @Value("${payup.iscd}")
  private String institutionCode;

  public PayupUtils(@Qualifier("payUpRestClient") RestClient restClient, ObjectMapper objectMapper) {
    this.restClient = restClient;
    this.objectMapper = objectMapper;
  }

  public boolean validateAccountHolder(Mentor mentor, Set<Mentor> failMentor) {
    PayupRequestHeader header = PayupRequestHeader.createAccountHolderRequestHeader(institutionCode, accessToken);
    RequiredAccountHolderData requiredAccountHolderData = RequiredAccountHolderData.of(header, mentor);
    try {
      String requestBody = objectMapper.writeValueAsString(requiredAccountHolderData);
      AccountHolderResult entity = restClient.post()
          .uri(NHRequestUrl.ACCOUNT_HOLDER_URI.getUrl())
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .body(requestBody)
          .exchange((request, response) -> checkStatusCode(response, AccountHolderResult.class));

      if (entity.validateResponseCode() && entity.isEqualToAccountName(mentor.getAccountName())) {
        return true;
      }

      log.warn(entity.getMessage());
      failMentor.add(mentor);
      return false;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }
  }

  public void requestPayup(Mentor mentor, Integer totalAmount) {
    PayupRequestHeader payupRequestHeader = PayupRequestHeader.createDepositRequestHeader(institutionCode, accessToken);
    RequiredDepositData requiredDepositData = RequiredDepositData.of(payupRequestHeader, mentor, totalAmount);

    try {
      String requestBody = objectMapper.writeValueAsString(requiredDepositData);
      DepositResult entity = restClient.post()
          .uri(NHRequestUrl.DEPOSIT_URI.getUrl())
          .accept(APPLICATION_JSON)
          .contentType(APPLICATION_JSON)
          .body(requestBody)
          .exchange((request, response) -> checkStatusCode(response, DepositResult.class));

      if (!entity.validateResponseCode()) {
        log.warn(entity.getMessage());
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException("직렬화 불가능한 객체거나 null입니다.");
    }
  }

  private <T extends PayupResult> T checkStatusCode(ConvertibleClientHttpResponse response, Class<T> clazz)
      throws IOException {
    HttpStatusCode statusCode = response.getStatusCode();
    if (statusCode.is2xxSuccessful()) {
      return objectMapper.readValue(response.getBody(), clazz);
    } else {
      throw new RuntimeException("서버 에러입니다.");
    }
  }

}