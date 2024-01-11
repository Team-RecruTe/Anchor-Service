package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;

import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Repository
public class QMentoringRepositoryImpl implements QMentoringRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public Page<MentoringSearchResult> findMentorings(List<String> tags, String keyword, Pageable pageable) {
    List<Long> mentoringIds = jpaQueryFactory.select(mentoring.id)
        .from(mentoring)
        .where(
            containsTitle(keyword)
                .or(containsContents(keyword))
                .and(equalTags(tags))
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Mentoring> result = jpaQueryFactory.selectFrom(mentoring)
        .leftJoin(mentoring.mentor)
        .fetchJoin()
        .leftJoin(mentoring.mentoringTags)
        .fetchJoin()
        .leftJoin(mentoring.mentor.user)
        .fetchJoin()
        .where(mentoring.id.in(mentoringIds))
        .orderBy(getOrderSpecifier(pageable.getSort()))
        .fetch();

    List<MentoringSearchResult> mentoringSearchResults = result.stream()
        .map(MentoringSearchResult::of)
        .toList();

    return new PageImpl<>(mentoringSearchResults, pageable, mentoringSearchResults.size());
  }

  public List<MentoringSearchResult> findTopMentorings() {
    List<Mentoring> result = jpaQueryFactory.selectFrom(mentoring)
        .orderBy(mentoring.totalApplicationNumber.desc())
        .limit(10)
        .leftJoin(mentoring.mentor)
        .fetchJoin()
        .leftJoin(mentoring.mentoringTags)
        .fetchJoin()
        .leftJoin(mentoring.mentor.user)
        .fetchJoin()
        .fetch();

    List<MentoringSearchResult> topMentorings = result.stream()
        .map(MentoringSearchResult::of)
        .toList();

    return topMentorings;
  }

  private OrderSpecifier[] getOrderSpecifier(Sort sort) {
    List<OrderSpecifier> orders = new ArrayList<>();
    sort.stream()
        .forEach(order -> {
          PathBuilder<Mentoring> orderByExpression = new PathBuilder<>(Mentoring.class, "mentoring");
          Order direction = order.isAscending() ? Order.ASC : Order.DESC;
          String prop = order.getProperty();
          orders.add(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
    return orders.toArray(OrderSpecifier[]::new);
  }

  private BooleanExpression containsTitle(String keyword) {
    if (StringUtils.hasText(keyword)) {
      return mentoring.title.contains(keyword);
    }
    return Expressions.TRUE;
  }

  private BooleanExpression containsContents(String keyword) {
    if (StringUtils.hasText(keyword)) {
      return mentoring.mentoringDetail.contents.contains(keyword);
    }
    return Expressions.TRUE;
  }

  private BooleanBuilder equalTags(List<String> tags) {
    BooleanBuilder builder = new BooleanBuilder();
    if (!CollectionUtils.isEmpty(tags)) {
      tags.stream()
          .map(this::equalTag)
          .forEach(builder::or);
    }
    return builder;
  }

  private BooleanExpression equalTag(String tag) {
    if (tag != null && !tag.isBlank()) {
      return mentoring.mentoringTags.any().tag.eq(tag);
    }
    return null;
  }

}
