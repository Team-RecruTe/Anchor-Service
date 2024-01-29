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
  private String requestDate = LocalDate.now()
      .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
  @JsonProperty("Trtm")
  private String requestTime = LocalTime.now()
      .format(DateTimeFormatter.ofPattern("HHmmss"));
  @JsonProperty("Iscd")
  private String institutionCode;
  @JsonProperty("FintechApsno")
  private String fintechApsno;
  @JsonProperty("ApiSvcCd")
  private String apiServiceCode;
  @JsonProperty("IsTuno")
  private String uniqueNumber = UUID.randomUUID()
      .toString()
      .replace("-", "")
      .substring(0, 20);
  @JsonProperty("AccessToken")
  private String accessToken;

  @Builder
  public PayupRequestHeader(String apiName, String institutionCode,
      String fintechApsno, String apiServiceCode, String accessToken) {
    this.apiName = apiName;
    this.institutionCode = institutionCode;
    this.fintechApsno = fintechApsno;
    this.apiServiceCode = apiServiceCode;
    this.accessToken = accessToken;
  }

}
