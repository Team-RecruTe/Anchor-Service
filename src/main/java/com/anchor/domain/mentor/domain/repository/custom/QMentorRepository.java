package com.anchor.domain.mentor.domain.repository.custom;

import java.time.LocalDateTime;

public interface QMentorRepository {

  void removeByIdAndProgressTime(
      Long mentorId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

}
