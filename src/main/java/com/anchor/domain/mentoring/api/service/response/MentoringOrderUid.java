package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MentoringOrderUid {

  @JsonProperty("order_uid")
  private String orderUid;

  public MentoringOrderUid(MentoringApplication mentoringApplication) {
    this.orderUid = mentoringApplication.getPayment()
        .getOrderUid();
  }
}
