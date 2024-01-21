package com.anchor.global.nhpay.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayupResponseHeader {

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
  @JsonProperty("Rpcd")
  private String responseCode;
  @JsonProperty("Rsms")
  private String responseMessage;

}
