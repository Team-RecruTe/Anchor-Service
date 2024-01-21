package com.anchor.global.nhpay.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PayupRequestHeader {

  @JsonProperty("ApiNm")
  private String apiName;
  @JsonProperty("Tsymd")
  private String requestDate;
  @JsonProperty("Trtm")
  private String requestTime;
  @JsonProperty("Iscd")
  private String institutionCode;
  @JsonProperty("FintechApsno")
  private String fintechApsno;
  @JsonProperty("ApiSvcCd")
  private String ApiServiceCode;
  @JsonProperty("IsTuno")
  private String uniqueNumber;
  @JsonProperty("AccessToken")
  private String accessToken;

  @Builder
  public PayupRequestHeader(String apiName, String requestDate, String requestTime, String institutionCode,
      String fintechApsno, String apiServiceCode, String uniqueNumber, String accessToken) {
    this.apiName = apiName;
    this.requestDate = requestDate;
    this.requestTime = requestTime;
    this.institutionCode = institutionCode;
    this.fintechApsno = fintechApsno;
    this.ApiServiceCode = apiServiceCode;
    this.uniqueNumber = uniqueNumber;
    this.accessToken = accessToken;
  }

  public static PayupRequestHeader createAccountHolderRequestHeader(String institutionCode, String accessToken) {
    String requestDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String requestTime = LocalTime.now()
        .format(DateTimeFormatter.ofPattern("HHmmss"));
    String uuid = UUID.randomUUID()
        .toString()
        .replace("-", "")
        .substring(0, 20);
    return PayupRequestHeader.builder()
        .requestDate(requestDate)
        .requestTime(requestTime)
        .apiName(NHHeaders.ACCOUNT_HOLDER_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.ACCOUNT_HOLDER_SEARCH_API_CODE.getValue())
        .uniqueNumber(uuid)
        .accessToken(accessToken)
        .build();
  }

  public static PayupRequestHeader createDepositRequestHeader(String institutionCode, String accessToken) {
    String requestDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    String requestTime = LocalTime.now()
        .format(DateTimeFormatter.ofPattern("HHmmss"));
    String uuid = UUID.randomUUID()
        .toString()
        .replace("-", "")
        .substring(0, 20);
    return PayupRequestHeader.builder()
        .requestDate(requestDate)
        .requestTime(requestTime)
        .apiName(NHHeaders.DEPOSIT_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.DEPOSIT_API_CODE.getValue())
        .uniqueNumber(uuid)
        .accessToken(accessToken)
        .build();
  }
}
