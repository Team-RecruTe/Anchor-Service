package com.anchor.domain.mentor.api.controller.request;

import com.anchor.global.util.type.DateTimeRange;
import com.anchor.global.util.valid.ValidRange;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringUnavailableTimeInfo {

  @ValidRange(message = "중복된 시간 범위가 존재합니다.")
  private List<DateTimeRange> dateTimeRanges;

}
