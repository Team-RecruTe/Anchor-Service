package com.anchor.domain.mentor.api.controller.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringUnavailableTimeInfos {

  private List<DateTimeRange> dateTimeRanges;

  @Getter
  @NoArgsConstructor
  public static class DateTimeRange {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime from;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime to;

    private DateTimeRange(LocalDateTime from, LocalDateTime to) {
      this.from = from;
      this.to = to;
    }

    public static DateTimeRange of(LocalDateTime from, LocalDateTime to) {
      return new DateTimeRange(from, to);
    }

  }

}
