package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentoringApplicationRepositoryImpl implements QMentoringApplicationRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public MentoringApplication findByMentoringIdAndProgressTime(Long mentoringId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .where(mentoringApplication.mentoring.id.eq(mentoringId)
            .and(mentoringApplication.startDateTime.eq(startDateTime))
            .and(mentoringApplication.endDateTime.eq(endDateTime)))
        .fetchOne();
  }

  @Override
  public List<MentoringApplication> findTimesByMentoringIdAndStatus(Long mentorId, MentoringStatus... statuses) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId)
            .and(statusOr(statuses)))
        .fetch();
  }

  private BooleanBuilder statusOr(MentoringStatus... statuses) {
    BooleanBuilder builder = new BooleanBuilder();
    Arrays.stream(statuses)
        .map(this::equalStatus)
        .forEach(builder::or);
    return builder;
  }

  private BooleanExpression equalStatus(MentoringStatus status) {
    return switch (status) {
      case CANCELLED -> mentoringApplication.mentoringStatus.eq(MentoringStatus.CANCELLED);
      case WAITING -> mentoringApplication.mentoringStatus.eq(MentoringStatus.WAITING);
      case COMPLETE -> mentoringApplication.mentoringStatus.eq(MentoringStatus.COMPLETE);
      case APPROVAL -> mentoringApplication.mentoringStatus.eq(MentoringStatus.APPROVAL);
    };
  }

}
