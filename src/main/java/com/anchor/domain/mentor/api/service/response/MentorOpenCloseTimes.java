package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.MentorSchedule;
import com.anchor.global.util.ResponseDto;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorOpenCloseTimes extends ResponseDto {

  private Map<String, List<OpenCloseTime>> days;

  private MentorOpenCloseTimes(Map<String, List<OpenCloseTime>> days) {
    this.days = days;
  }

  public static MentorOpenCloseTimes of(List<MentorSchedule> mentorSchedules) {
    Map<String, List<OpenCloseTime>> days = new HashMap<>();
    Arrays.stream(DayOfWeek.values())
        .forEach(day -> {
          days.put(day.name(), new ArrayList<>());
        });

    mentorSchedules
        .forEach(schedule -> {
          LocalTime openTime = schedule.getOpenTime();
          LocalTime closeTime = schedule.getCloseTime();
          HourMinute openHM = new HourMinute(openTime.getHour(), openTime.getMinute());
          HourMinute closeHM = new HourMinute(closeTime.getHour(), closeTime.getMinute());
          OpenCloseTime openCloseTime = new OpenCloseTime(openHM, closeHM);
          DayOfWeek day = schedule.getDayOfWeek();
          days.get(day.name())
              .add(openCloseTime);
        });

    return new MentorOpenCloseTimes(days);
  }

  @Getter
  @NoArgsConstructor
  public static class OpenCloseTime {

    private HourMinute open;
    private HourMinute close;

    private OpenCloseTime(HourMinute open, HourMinute close) {
      this.open = open;
      this.close = close;
    }

  }

  @Getter
  @NoArgsConstructor
  public static class HourMinute {

    private Integer hour;
    private Integer minute;

    private HourMinute(Integer hour, Integer minute) {
      this.hour = hour;
      this.minute = minute;
    }

  }

}