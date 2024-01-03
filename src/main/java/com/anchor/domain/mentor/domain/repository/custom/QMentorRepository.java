package com.anchor.domain.mentor.domain.repository.custom;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;

public interface QMentorRepository {

  List<MentoringUnavailableTime> findUnavailableTimes(Long mentorId);

  void deleteUnavailableTimes(Long mentorId);

  void saveUnavailableTimes(Long mentorId, List<DateTimeRange> unavailableTimes);

  void saveUnavailableTimesWithBatch(Long mentorId, List<DateTimeRange> unavailableTimes);

}
