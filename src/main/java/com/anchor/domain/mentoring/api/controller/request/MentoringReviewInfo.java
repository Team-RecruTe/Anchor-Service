package com.anchor.domain.mentoring.api.controller.request;

import com.anchor.global.util.type.DateTimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class MentoringReviewInfo {

  @NotBlank(message = "상세 내용을 입력해주세요.")
  @Size(min = 1, max = 200, message = "1자 이상 200자 이하로 작성해주세요.")
  private String contents;

  private Integer ratings;

  private DateTimeRange timeRange;

  @Builder
  @ConstructorProperties({"contents", "ratings", "startTime", "endTime"})
  private MentoringReviewInfo(String contents, Integer ratings, String startTime, String endTime) {
    this.contents = contents;
    this.ratings = ratings;
    if (StringUtils.hasText(startTime) && StringUtils.hasText(endTime)) {
      LocalDateTime from = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      LocalDateTime to = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      this.timeRange = DateTimeRange.of(from, to);
    }
  }

}
