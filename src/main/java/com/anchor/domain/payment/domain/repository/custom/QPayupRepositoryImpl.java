package com.anchor.domain.payment.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.payment.domain.QPayment.payment;
import static com.anchor.domain.payment.domain.QPayup.payup;
import static com.anchor.domain.user.domain.QUser.user;

import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.domain.payment.domain.PayupStatus;
import com.anchor.global.util.type.DateTimeRange;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
                payup.amount
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
                .and(payup.payupStatus.eq(PayupStatus.COMPLETE))
        )
        .orderBy(mentoringApplication.startDateTime.asc())
        .fetch();
  }

}
