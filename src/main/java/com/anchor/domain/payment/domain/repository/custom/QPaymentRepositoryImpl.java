package com.anchor.domain.payment.domain.repository.custom;

import static com.anchor.domain.payment.domain.QPayment.payment;

import com.anchor.domain.payment.domain.Payment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
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
}
