package com.anchor.domain.payment.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredPaymentInfo implements Serializable {

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime startDateTime;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime endDateTime;

  private String impUid;

  private String merchantUid;

  private Integer amount;


  @Builder
  private RequiredPaymentInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, String impUid, String merchantUid,
      Integer amount) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.amount = amount;
  }
}
