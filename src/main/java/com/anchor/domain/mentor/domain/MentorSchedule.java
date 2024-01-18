package com.anchor.domain.mentor.domain;

import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes.HourMinute;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes.OpenCloseTime;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  public MentorSchedule(DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime) {
    this.activeStatus = ActiveStatus.OPEN;
    this.dayOfWeek = dayOfWeek;
    this.openTime = openTime;
    this.closeTime = closeTime;
  }

  public static List<MentorSchedule> of(MentorOpenCloseTimes mentorOpenCloseTimes) {
    List<MentorSchedule> mentorSchedules = new ArrayList<>();
    Map<String, List<OpenCloseTime>> scheduleOfDays = mentorOpenCloseTimes.getDays();

    Arrays.stream(DayOfWeek.values())
        .forEach(day -> {
          List<OpenCloseTime> openCloseTimes = scheduleOfDays.get(day.name());
          openCloseTimes.forEach(time -> {
            HourMinute openHM = time.getOpen();
            HourMinute closeHM = time.getClose();
            LocalTime openTime = LocalTime.of(openHM.getHour(), openHM.getMinute());
            LocalTime closeTime = LocalTime.of(closeHM.getHour(), closeHM.getMinute());
            mentorSchedules.add(new MentorSchedule(day, openTime, closeTime));
          });
        });
    return mentorSchedules;
  }
}
