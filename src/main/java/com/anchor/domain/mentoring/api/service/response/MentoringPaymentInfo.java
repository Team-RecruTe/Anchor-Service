package com.anchor.domain.mentoring.api.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentoringPaymentInfo implements Serializable {

  @JsonProperty("name")
  private final String name = "Anchor 멘토링결제";

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime startDateTime;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime endDateTime;

  private Integer amount;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  @JsonProperty("imp_code")
  private String impCode;

  @JsonProperty("buyer_tel")
  private String buyerTel;

  @Builder
  public MentoringPaymentInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer amount,
      String merchantUid,
      String impCode, String buyerTel) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.amount = amount;
    this.merchantUid = merchantUid;
    this.impCode = impCode;
    this.buyerTel = buyerTel;
  }
}
