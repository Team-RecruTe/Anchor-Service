package com.anchor.domain.mentoring.api.controller.request;

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
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringApplicationInfo implements Serializable {

  @JsonProperty("imp_uid")
  private String impUid;

  @JsonProperty("merchant_uid")
  private String merchantUid;

  private Integer amount;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime startDateTime;

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime endDateTime;

  @Builder
  private MentoringApplicationInfo(String impUid, String merchantUid, Integer amount, LocalDateTime startDateTime,
      LocalDateTime endDateTime) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
  }
}
