package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.mentoring.domain.QMentoringReview.mentoringReview;

import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.global.util.type.DateTimeRange;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QMentoringReviewRepositoryImpl implements QMentoringReviewRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public MentoringReview findByTimeRange(DateTimeRange dateTimeRange, Long userId) {
    return jpaQueryFactory.selectFrom(mentoringReview)
        .leftJoin(mentoringReview.mentoringApplication, mentoringApplication)
        .where(
            mentoringApplication.user.id.eq(userId)
                .and(mentoringApplication.startDateTime.eq(dateTimeRange.getFrom()))
                .and(mentoringApplication.endDateTime.eq(dateTimeRange.getTo()))
        )
        .fetchOne();
  }

}
