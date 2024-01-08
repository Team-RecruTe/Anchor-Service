package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QMentoringApplicationRepository {

  MentoringApplication findByMentoringIdAndProgressTime(
      Long mentoringId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  Optional<MentoringApplication> findAppliedMentoringByTimeAndUserId
      (LocalDateTime startDateTime, LocalDateTime endDateTime, Long userId);

  List<MentoringApplication> findUnavailableTimesByMentoringIdAndStatus(Long mentorId, MentoringStatus... statuses);

  Page<AppliedMentoringSearchResult> findAllByMentorId(Long mentorId, Pageable pageable);

  Optional<MentoringApplication> findMentoringApplicationByTimeRangeAndUserId
      (LocalDateTime startDateTime, LocalDateTime endDateTime, Long userId);

  Optional<MentoringApplication> findMentoringApplicationByMentoringId
      (LocalDateTime startDateTime, LocalDateTime endDateTime, Long mentoringId);

    List<MentoringApplication> findPayupListByCompleteAndLastMonth(MentoringStatus status, LocalDateTime lastMonth, LocalDateTime thisMonth);
}
