package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentor.domain.ActiveStatus;
import com.anchor.domain.mentor.domain.MentorSchedule;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.global.util.type.DateTimeRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationTimeInfo {


  private List<DateTimeRange> unavailableTimes;

  private List<MentorActiveTime> activeTimes;

  private ApplicationTimeInfo(List<DateTimeRange> unavailableTimes, List<MentorActiveTime> activeTimes) {
    this.unavailableTimes = unavailableTimes;
    this.activeTimes = activeTimes;
  }

  public static ApplicationTimeInfo create(List<MentoringApplication> applications, List<MentorSchedule> schedules,
      List<DateTimeRange> paymentTimes) {
    List<MentorActiveTime> mentorActiveTimes = schedules.stream()
        .map(MentorActiveTime::of)
        .toList();

    List<DateTimeRange> unavailableTimes = new ArrayList<>();
    applications.stream()
        .map(application -> DateTimeRange.of(application.getStartDateTime(), application.getEndDateTime()))
        .forEach(unavailableTimes::add);

    unavailableTimes.addAll(paymentTimes);
    return new ApplicationTimeInfo(unavailableTimes, mentorActiveTimes);
  }

  @Getter
  @NoArgsConstructor
  static class MentorActiveTime {

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @JsonProperty("open_time")
    private LocalTime openTime;

    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    @JsonProperty("close_time")
    private LocalTime closeTime;

    @JsonProperty("day_of_week")
    private DayOfWeek dayOfWeek;

    @JsonProperty("active_status")
    private ActiveStatus activeStatus;


    private MentorActiveTime(LocalTime openTime, LocalTime closeTime, DayOfWeek dayOfWeek, ActiveStatus activeStatus) {
      this.openTime = openTime;
      this.closeTime = closeTime;
      this.dayOfWeek = dayOfWeek;
      this.activeStatus = activeStatus;
    }

    public static MentorActiveTime of(MentorSchedule mentorSchedule) {
      return new MentorActiveTime(mentorSchedule.getOpenTime(), mentorSchedule.getCloseTime(),
          mentorSchedule.getDayOfWeek(), mentorSchedule.getActiveStatus());
    }
  }

}
