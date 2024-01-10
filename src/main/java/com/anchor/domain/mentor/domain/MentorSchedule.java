package com.anchor.domain.mentor.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(uniqueConstraints = {
    @UniqueConstraint(name = "DayOfWeek_Mentor_CK", columnNames = {
        "mentor_id",
        "day_of_week"
    }),
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentorSchedule extends BaseEntity {

  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;

  private LocalTime openTime;

  private LocalTime closeTime;

  @Enumerated(EnumType.STRING)
  private ActiveStatus activeStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof MentorSchedule other) {
      return other.hashCode() == hashCode();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(dayOfWeek, mentor);
  }

}
