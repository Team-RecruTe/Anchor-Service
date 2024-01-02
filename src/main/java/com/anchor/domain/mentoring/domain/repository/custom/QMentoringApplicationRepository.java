package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import java.time.LocalDateTime;
import java.util.Optional;

public interface QMentoringApplicationRepository {

  Optional<MentoringApplication> findByIdAndProgressTime(
      Long mentoringId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

}
