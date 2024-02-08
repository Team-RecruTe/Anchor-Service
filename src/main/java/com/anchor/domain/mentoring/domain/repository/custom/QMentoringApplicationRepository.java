package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QMentoringApplicationRepository {

  MentoringApplication findByMentorIdAndProgressTime(
      Long mentorId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  Optional<MentoringApplication> findByUserIdAndProgressTime(
      Long userId,
      LocalDateTime startDateTime,
      LocalDateTime endDateTime
  );

  Page<AppliedMentoringSearchResult> findAllByMentorId(Long mentorId, Pageable pageable);

  List<MentoringApplication> findAllByMentorId(Long mentorID);

  Page<AppliedMentoringInfo> findByUserId(Long userId, Pageable pageable);

  List<MentoringApplication> findAllByNotCompleteForWeek(DateTimeRange weekAgoDate);

  Long getMentoringId(MentoringApplication application);

}
