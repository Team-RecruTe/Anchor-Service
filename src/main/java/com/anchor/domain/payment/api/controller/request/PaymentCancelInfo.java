package com.anchor.domain.payment.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancelInfo implements Serializable {

  private List<RequiredMentoringCancelInfo> mentoringCancelInfos;

  @Builder
  private PaymentCancelInfo(List<RequiredMentoringCancelInfo> mentoringCancelInfos) {
    this.mentoringCancelInfos = mentoringCancelInfos;
  }

  @Getter
  @NoArgsConstructor
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class RequiredMentoringCancelInfo {

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDateTime;

    @JsonFormat(shape = Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDateTime;

    private Long mentoringId;

    private String cancelReason;

    @Builder
    private RequiredMentoringCancelInfo(LocalDateTime startDateTime, LocalDateTime endDateTime, Long mentoringId,
        String cancelReason) {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
      this.mentoringId = mentoringId;
      this.cancelReason = cancelReason;
    }
  }
}
