package com.anchor.domain.user.api.controller.request;

import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringStatusInfo {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
  private LocalDateTime requestTime;

  private List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos;

  @Builder
  private MentoringStatusInfo(LocalDateTime requestTime,
      List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    this.requestTime = requestTime;
    this.requiredMentoringStatusInfos = requiredMentoringStatusInfos;
  }

  @Getter
  @NoArgsConstructor
  public static class RequiredMentoringStatusInfo {

    private DateTimeRange mentoringReservedTime;

    private MentoringStatus mentoringStatus;

    @Builder
    private RequiredMentoringStatusInfo(DateTimeRange mentoringReservedTime, MentoringStatus mentoringStatus) {
      this.mentoringReservedTime = mentoringReservedTime;
      this.mentoringStatus = mentoringStatus;
    }

  }

}
