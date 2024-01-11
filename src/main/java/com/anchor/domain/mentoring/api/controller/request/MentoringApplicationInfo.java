package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  private LocalDateTime startDateTime;

  @JsonIgnore
  private LocalDateTime endDateTime;

  @Builder
  private MentoringApplicationInfo(String impUid, String merchantUid, Integer amount) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
  }

  public void addApplicationTime(DateTimeRange dateTimeRange) {
    this.startDateTime = dateTimeRange.getFrom();
    this.endDateTime = dateTimeRange.getTo();
  }
}
