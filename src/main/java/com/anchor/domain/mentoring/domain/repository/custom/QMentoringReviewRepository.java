package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.global.util.type.DateTimeRange;

public interface QMentoringReviewRepository {

  MentoringReview findByTimeRange(DateTimeRange dateTimeRange, Long userId);
}
