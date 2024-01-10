package com.anchor.domain.mentor.domain.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentorRepositoryImpl implements QMentorRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final JdbcTemplate jdbcTemplate;

}
