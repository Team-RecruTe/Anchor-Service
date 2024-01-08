package com.anchor.domain.mentoring.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime startDateTime;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
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
