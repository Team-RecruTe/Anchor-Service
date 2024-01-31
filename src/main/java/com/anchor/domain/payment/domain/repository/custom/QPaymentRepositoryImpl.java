package com.anchor.domain.payment.domain.repository.custom;

import static com.anchor.domain.mentor.domain.QMentor.mentor;
import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;
import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.payment.domain.QPayment.payment;
import static com.anchor.domain.user.domain.QUser.user;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.api.service.response.PaymentCompleteResult;
import com.anchor.domain.payment.domain.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QPaymentRepositoryImpl implements QPaymentRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<Payment> findPaymentListStartWithToday(String today) {
    return jpaQueryFactory.selectFrom(payment)
        .where(payment.merchantUid.startsWith("toss_" + today))
        .fetch();
  }

  @Override
  public PaymentCompleteResult findCompletedPaymentByOrderUid(String orderUid) {
    MentoringApplication result = jpaQueryFactory.selectFrom(mentoringApplication)
        .leftJoin(mentoringApplication.payment)
        .fetchJoin()
        .leftJoin(mentoringApplication.mentoring, mentoring)
        .fetchJoin()
        .leftJoin(mentoring.mentor, mentor)
        .fetchJoin()
        .leftJoin(mentor.user, user)
        .fetchJoin()
        .where(
            mentoringApplication.payment.orderUid.eq(orderUid)
        )
        .fetchOne();

    return PaymentCompleteResult.of(Objects.requireNonNull(result));
  }
}
