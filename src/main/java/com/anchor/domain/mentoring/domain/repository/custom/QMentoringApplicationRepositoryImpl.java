package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentoringApplicationRepositoryImpl implements QMentoringApplicationRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Optional<MentoringApplication> findByIdAndProgressTime(Long mentoringId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return jpaQueryFactory.select(mentoringApplication)
        .where(mentoringApplication.mentoring.id.eq(mentoringId))
        .where(mentoringApplication.startDateTime.eq(startDateTime))
        .where(mentoringApplication.endDateTime.eq(endDateTime))
        .stream()
        .findFirst();
  }

}
