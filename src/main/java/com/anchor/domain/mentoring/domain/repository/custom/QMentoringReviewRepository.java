package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;

public interface QMentoringReviewRepository {

  MentoringReview findByTimeRange(DateTimeRange dateTimeRange, Long userId);

  List<MentoringReview> findAllByMentoringId(Long mentoringId);
}
