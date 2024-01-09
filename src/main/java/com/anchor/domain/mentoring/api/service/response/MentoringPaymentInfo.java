package com.anchor.domain.mentoring.api.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MentoringPaymentInfo implements Serializable {

  @JsonProperty("name")
  private final String name = "Anchor 멘토링결제";

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime startDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
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
