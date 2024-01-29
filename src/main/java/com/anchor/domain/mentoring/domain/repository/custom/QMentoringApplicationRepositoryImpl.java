package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentor.domain.QMentor.mentor;
import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;
import static com.anchor.domain.mentoring.domain.QMentoringApplication.mentoringApplication;

import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.global.util.type.DateTimeRange;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentoringApplicationRepositoryImpl implements QMentoringApplicationRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public MentoringApplication findByMentorIdAndProgressTime(Long mentorId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId)
            .and(mentoringApplication.startDateTime.eq(startDateTime))
            .and(mentoringApplication.endDateTime.eq(endDateTime)))
        .fetchOne();
  }

  public Page<AppliedMentoringSearchResult> findAllByMentorId(Long mentorId, Pageable pageable) {
    Long totalElements = jpaQueryFactory.select(mentoringApplication.count())
        .from(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId))
        .fetchOne();

    List<Long> keys = jpaQueryFactory.select(mentoringApplication.id)
        .from(mentoringApplication)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(getOrderSpecifier(pageable.getSort()))
        .fetch();

    List<MentoringApplication> results = jpaQueryFactory.selectFrom(mentoringApplication)
        .leftJoin(mentoringApplication.mentoring)
        .fetchJoin()
        .leftJoin(mentoringApplication.user)
        .fetchJoin()
        .leftJoin(mentoringApplication.payment)
        .fetchJoin()
        .where(mentoringApplication.id.in(keys))
        .orderBy(getOrderSpecifier(pageable.getSort()))
        .fetch();

    List<AppliedMentoringSearchResult> appliedMentoringSearchResults = results.stream()
        .map(result -> AppliedMentoringSearchResult.of(result))
        .toList();

    return new PageImpl<>(appliedMentoringSearchResults, pageable, totalElements);
  }

  private OrderSpecifier[] getOrderSpecifier(Sort sort) {
    List<OrderSpecifier> orders = new ArrayList<>();
    sort.stream()
        .forEach(order -> {
          PathBuilder<MentoringApplication> orderByExpression = new PathBuilder<>(MentoringApplication.class,
              "mentoringApplication");
          Order direction = order.isAscending() ? Order.ASC : Order.DESC;
          String prop = order.getProperty();
          orders.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
    return orders.toArray(OrderSpecifier[]::new);
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
  public List<MentoringApplication> findAllByMentorId(Long mentorId) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .leftJoin(mentoringApplication.mentoring, mentoring)
        .leftJoin(mentoring.mentor, mentor)
        .where(mentoringApplication.mentoring.mentor.id.eq(mentorId))
        .fetch();
  }

  @Override
  public Page<AppliedMentoringInfo> findByUserId(Long userId, Pageable pageable) {
    Long totalElements = jpaQueryFactory.select(mentoringApplication.count())
        .from(mentoringApplication)
        .where(mentoringApplication.user.id.eq(userId))
        .fetchOne();

    List<Long> keys = jpaQueryFactory.select(mentoringApplication.id)
        .from(mentoringApplication)
        .where(mentoringApplication.user.id.eq(userId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<MentoringApplication> result = jpaQueryFactory.selectFrom(mentoringApplication)
        .join(mentoringApplication.mentoring)
        .fetchJoin()
        .join(mentoringApplication.payment)
        .fetchJoin()
        .join(mentoring.mentor)
        .fetchJoin()
        .join(mentor.user)
        .fetchJoin()
        .where(mentoringApplication.id.in(keys))
        .fetch();

    List<AppliedMentoringInfo> appliedMentoringInfos = result.stream()
        .map(AppliedMentoringInfo::of)
        .toList();

    return new PageImpl<>(appliedMentoringInfos, pageable, totalElements);
  }

  public Long getMentoringId(MentoringApplication application) {
    return jpaQueryFactory.select(mentoringApplication.mentoring.id)
        .from(mentoringApplication)
        .where(mentoringApplication.eq(application))
        .fetchOne();
  }

  @Override
  public List<MentoringApplication> findAllByNotCompleteForWeek(DateTimeRange weekAgoDate) {
    return jpaQueryFactory.selectFrom(mentoringApplication)
        .where(
            mentoringApplication.mentoringStatus.eq(MentoringStatus.APPROVAL)
                .and(mentoringApplication.endDateTime.goe(weekAgoDate.getFrom()))
                .and(mentoringApplication.endDateTime.before(weekAgoDate.getTo())))
        .fetch();
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