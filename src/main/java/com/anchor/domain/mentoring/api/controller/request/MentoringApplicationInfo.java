package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
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

  @JsonProperty("reserved_time")
  private DateTimeRange reservedTime;

  @Builder
  private MentoringApplicationInfo(String impUid, String merchantUid, Integer amount, DateTimeRange reservedTime) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
    this.reservedTime = reservedTime;
  }

}
