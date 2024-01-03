package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import java.time.LocalDateTime;
import java.util.List;

public interface QMentoringApplicationRepository {

  MentoringApplication findByMentoringIdAndProgressTime(
      Long mentoringId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  List<MentoringApplication> findTimesByMentoringIdAndStatus(Long mentorId, MentoringStatus... statuses);

}
