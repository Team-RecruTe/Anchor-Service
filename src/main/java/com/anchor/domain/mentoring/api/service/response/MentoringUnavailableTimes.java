package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.global.util.ResponseDto;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringUnavailableTimes extends ResponseDto {

  private List<DateTimeRange> dateTimeRanges;

  private MentoringUnavailableTimes(List<DateTimeRange> dateTimeRanges) {
    this.dateTimeRanges = dateTimeRanges;
  }

  public static MentoringUnavailableTimes of(List<MentoringUnavailableTime> unavailableTimes,
      List<MentoringApplication> reservedMentorings) {
    List<DateTimeRange> ranges = new ArrayList<>();

    ranges.addAll(unavailableTimes.stream()
        .map(unavailableTime -> {
          LocalDateTime from = unavailableTime.getFromDateTime();
          LocalDateTime to = unavailableTime.getToDateTime();
          return DateTimeRange.of(from, to);
        })
        .toList());

    ranges.addAll(reservedMentorings.stream()
        .map(reservedTime -> {
          LocalDateTime from = reservedTime.getStartDateTime();
          LocalDateTime to = reservedTime.getEndDateTime();
          return DateTimeRange.of(from, to);
        })
        .toList());

    return new MentoringUnavailableTimes(ranges);
  }

}
