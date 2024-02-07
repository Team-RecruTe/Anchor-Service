package com.anchor.domain.mentor.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.domain.Mentor;

public interface QMentorRepository {

  Mentor findMentorInfoById(Long mentorId);

  MentorOpenCloseTimes findScheduleById(Long mentorId);

  void saveMentoSchedules(Long mentorId, MentorOpenCloseTimes mentorOpenCloseTimes);

  void deleteAllSchedules(Long mentorId);
}
