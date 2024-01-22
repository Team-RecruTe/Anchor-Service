package com.anchor.domain.mentor.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;

public interface QMentorRepository {

  MentorOpenCloseTimes findScheduleById(Long mentorId);

  void saveMentoSchedules(Long mentorId, MentorOpenCloseTimes mentorOpenCloseTimes);

  void deleteAllSchedules(Long mentorId);
}
