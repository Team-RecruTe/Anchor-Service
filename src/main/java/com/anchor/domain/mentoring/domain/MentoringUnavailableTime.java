package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentor.domain.Mentor;
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

  @Column(nullable = false, columnDefinition = "datetime")
  private LocalDateTime fromDateTime;

  @Column(nullable = false, columnDefinition = "datetime")
  private LocalDateTime toDateTime;

  @Column(name = "mentor_id", nullable = false)
  private Long mentorId;

  private MentoringUnavailableTime(LocalDateTime fromDateTime, LocalDateTime toDateTime) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
  }

  public MentoringUnavailableTime(MentoringApplication application, Mentoring mentoring) {
    this.fromDateTime = application.getStartDateTime();
    this.toDateTime = application.getEndDateTime();
    this.mentorId = mentoring.getMentor()
        .getId();
  }

  public MentoringUnavailableTime(LocalDateTime fromDateTime, LocalDateTime toDateTime, Mentor mentor) {
    this.fromDateTime = fromDateTime;
    this.toDateTime = toDateTime;
    this.mentorId = mentor.getId();
  }

  public static List<MentoringUnavailableTime> of(List<DateTimeRange> dateTimeRanges) {
    return dateTimeRanges.stream()
        .map(dateTimeRange -> new MentoringUnavailableTime(dateTimeRange.getFrom(),
            dateTimeRange.getTo()))
        .toList();
  }
}