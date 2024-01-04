package com.anchor.domain.payment.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QPaymentRepositoryImpl implements QPaymentRepository {

  private final JPAQueryFactory jpaQueryFactory;
}
