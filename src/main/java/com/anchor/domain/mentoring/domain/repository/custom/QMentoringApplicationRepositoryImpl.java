package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;
import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;
import static com.anchor.domain.payment.domain.QPayment.payment;

import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

  public Page<AppliedMentoringSearchResult> findAllByMentorId(Long mentorId, Pageable pageable) {
    List<Long> keys = jpaQueryFactory.select(mentoringApplication.id)
        .from(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    System.out.println(keys);

    List<MentoringApplication> result = jpaQueryFactory.selectFrom(mentoringApplication)
        .innerJoin(mentoringApplication.mentoring)
        .fetchJoin()
        .innerJoin(mentoringApplication.user)
        .fetchJoin()
        .innerJoin(mentoringApplication.payment)
        .fetchJoin()
        .where(mentoringApplication.id.in(keys))
        .fetch();

    List<AppliedMentoringSearchResult> appliedMentoringSearchResults = result.stream()
        .map(AppliedMentoringSearchResult::of)
        .toList();

    return new PageImpl<>(appliedMentoringSearchResults, pageable, appliedMentoringSearchResults.size());
  }

  @Override
  public Optional<MentoringApplication> findAppliedMentoringByTimeAndUserId(
      LocalDateTime startDateTime,
      LocalDateTime endDateTime, Long userId) {

    return Optional.ofNullable(
        jpaQueryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.mentoring, mentoring)
            .fetchJoin()
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(mentoringApplication.startDateTime.eq(startDateTime)
                .and(mentoringApplication.endDateTime.eq(endDateTime))
                .and(mentoringApplication.user.id.eq(userId))
            )
            .fetchOne());

  }

  @Override
  public List<MentoringApplication> findUnavailableTimesByMentoringIdAndStatus(Long mentorId,
      MentoringStatus... statuses) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId)
            .and(equalsStatuses(statuses)))
        .fetch();
  }

  @Override
  public Optional<MentoringApplication> findMentoringApplicationByTimeRangeAndUserId
      (LocalDateTime startDateTime, LocalDateTime endDateTime, Long userId) {
    return Optional.ofNullable(
        jpaQueryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(mentoringApplication.startDateTime.eq(startDateTime)
                .and(mentoringApplication.endDateTime.eq(endDateTime))
                .and(mentoringApplication.user.id.eq(userId)))
            .fetchOne()
    );
  }

  @Override
  public Optional<MentoringApplication> findMentoringApplicationByMentoringId(
      LocalDateTime startDateTime,
      LocalDateTime endDateTime, Long mentoringId) {

    return Optional.ofNullable(
        jpaQueryFactory.selectFrom(mentoringApplication)
            .join(mentoringApplication.payment, payment)
            .fetchJoin()
            .where(mentoringApplication.startDateTime.eq(startDateTime)
                .and(mentoringApplication.endDateTime.eq(endDateTime))
                .and(mentoringApplication.mentoring.id.eq(mentoringId)))
            .fetchOne()
    );
  }

  private BooleanBuilder equalsStatuses(MentoringStatus... statuses) {
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
