package com.anchor.domain.mentor.api.controller.request;

import com.anchor.global.util.DateTimeRange;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringUnavailableTimeInfos {

  private List<DateTimeRange> dateTimeRanges;

}
