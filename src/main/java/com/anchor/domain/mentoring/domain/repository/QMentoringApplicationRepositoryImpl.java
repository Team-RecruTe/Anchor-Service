package com.anchor.domain.mentoring.domain.repository;

import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;
import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.payment.domain.QPayment.payment;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QMentoringApplicationRepositoryImpl implements QMentoringApplicationRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<MentoringApplication> findAppliedMentoringByTimeAndUserId(LocalDateTime startDateTime,
      LocalDateTime endDateTime, Long userId) {

    return Optional.ofNullable(
        queryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.mentoring, mentoring)
            .fetchJoin()
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(
                mentoringApplication.startDateTime.eq(startDateTime)
                    .and(mentoringApplication.endDateTime.eq(endDateTime))
                    .and(mentoringApplication.user.id.eq(userId))
            )
            .fetchOne());

  }

  @Override
  public Optional<MentoringApplication> findMentoringApplicationByTimeRangeAndUserId
      (LocalDateTime startDateTime, LocalDateTime endDateTime, Long userId) {
    return Optional.ofNullable(
        queryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(mentoringApplication.startDateTime.eq(startDateTime)
                .and(mentoringApplication.endDateTime.eq(endDateTime))
                .and(mentoringApplication.user.id.eq(userId)))
            .fetchOne()
    );
  }

  @Override
  public Optional<MentoringApplication> findMentoringApplicationByMentoringId(LocalDateTime startDateTime,
      LocalDateTime endDateTime, Long mentoringId) {

    return Optional.ofNullable(
        queryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(mentoringApplication.startDateTime.eq(startDateTime)
                .and(mentoringApplication.endDateTime.eq(endDateTime))
                .and(mentoringApplication.mentoring.id.eq(mentoringId)))
            .fetchOne()
    );
  }
}
