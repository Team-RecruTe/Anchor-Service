package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationUserInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.global.util.type.DateTimeRange;
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
  @JsonProperty("buyer_email")
  private String buyerEmail;
  @JsonProperty("buyer_name")
  private String buyerName;

  @Builder
  private MentoringPaymentInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, Integer amount,
      String merchantUid,
      String impCode, String buyerName, String buyerEmail, String buyerTel) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.amount = amount;
    this.merchantUid = merchantUid;
    this.impCode = impCode;
    this.buyerName = buyerName;
    this.buyerEmail = buyerEmail;
    this.buyerTel = buyerTel;
  }

  public static MentoringPaymentInfo of(Mentoring mentoring, DateTimeRange dateTimeRange,
      MentoringApplicationUserInfo userInfo,
      String merchantUid, String impCode) {
    return MentoringPaymentInfo.builder()
        .startDateTime(dateTimeRange.getFrom())
        .endDateTime(dateTimeRange.getTo())
        .amount(mentoring.getCost())
        .merchantUid(merchantUid)
        .impCode(impCode)
        .buyerName(userInfo.getNickname())
        .buyerEmail(userInfo.getEmail())
        .buyerTel(userInfo.getTel())
        .build();
  }
}
