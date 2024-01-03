package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringUnavailableTime extends BaseEntity {

  @Column(nullable = false, columnDefinition = "datetime(6)")
  private LocalDateTime fromDateTime;

  @Column(nullable = false, columnDefinition = "datetime(6)")
  private LocalDateTime toDateTime;

  @Column(name = "mentor_id", nullable = false)
  private Long mentorId;

  private MentoringUnavailableTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
  }

  public static List<MentoringUnavailableTime> of(List<DateTimeRange> dateTimeRanges) {
    return dateTimeRanges.stream()
        .map(dateTimeRange -> new MentoringUnavailableTime(dateTimeRange.getFrom(),
            dateTimeRange.getTo()))
        .toList();
  }

}
