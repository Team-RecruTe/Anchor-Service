package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QMentoringApplicationRepository {

  MentoringApplication findByMentoringIdAndProgressTime(
      Long mentoringId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  List<MentoringApplication> findUnavailableTimesByMentoringIdAndStatus(Long mentorId, MentoringStatus... statuses);

  Page<AppliedMentoringSearchResult> findAllByMentorId(Long mentorId, Pageable pageable);

  List<MentoringApplication> findPayupListByCompleteAndLastMonth(MentoringStatus status, LocalDateTime lastMonth,
      LocalDateTime thisMonth);

  List<MentoringApplication> findByMentoringId(Long mentoringId);

  Page<AppliedMentoringInfo> findByUserId(Long userId, Pageable pageable);
}
