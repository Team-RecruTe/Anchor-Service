package com.anchor.domain.payment.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.payment.domain.QPayment.payment;
import static com.anchor.domain.payment.domain.QPayup.payup;
import static com.anchor.domain.user.domain.QUser.user;

import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.PayupStatus;
import com.anchor.global.util.type.DateTimeRange;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QPayupRepositoryImpl implements QPayupRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<PayupInfo> findAllByMonthRange(DateTimeRange dateTimeRange, Long mentorId) {
    return jpaQueryFactory.select(
            Projections.constructor(
                PayupInfo.class,
                mentoringApplication.startDateTime,
                mentoringApplication.endDateTime,
                user.nickname,
                payment.amount,
                payup.amount,
                payup.payupStatus
            )
        )
        .from(payup)
        .leftJoin(payup.payment, payment)
        .leftJoin(payment.mentoringApplication)
        .leftJoin(mentoringApplication.user, user)
        .where(
            payup.mentor.id.eq(mentorId)
                .and(mentoringApplication.startDateTime.goe(dateTimeRange.getFrom()))
                .and(mentoringApplication.endDateTime.before(dateTimeRange.getTo()))
        )
        .orderBy(mentoringApplication.startDateTime.asc())
        .fetch();
  }

  @Override
  public List<Payup> findAllByMonthRange(DateTimeRange dateTimeRange) {
    return jpaQueryFactory.selectFrom(payup)
        .join(payup.mentor)
        .fetchJoin()
        .join(mentoringApplication)
        .on(mentoringApplication.payment.id.eq(payup.payment.id))
        .where(
            mentoringApplication.mentoringStatus.eq(MentoringStatus.COMPLETE)
                .and(payup.payupStatus.eq(PayupStatus.WAITING))
                .and(payup.createDate.goe(dateTimeRange.getFrom()))
                .and(payup.createDate.before(dateTimeRange.getTo()))
        )
        .fetch();
  }

  @Override
  public void updateStatus(DateTimeRange dateTimeRange, Set<Mentor> failMentors) {
    List<Long> failMentorIds = failMentors.stream()
        .map(Mentor::getId)
        .toList();
    jpaQueryFactory.update(payup)
        .set(payup.payupStatus, PayupStatus.COMPLETE)
        .where(
            payup.createDate.goe(dateTimeRange.getFrom())
                .and(payup.createDate.before(dateTimeRange.getTo()))
                .and(payup.mentor.id.notIn(failMentorIds)))
        .execute();
  }
}
