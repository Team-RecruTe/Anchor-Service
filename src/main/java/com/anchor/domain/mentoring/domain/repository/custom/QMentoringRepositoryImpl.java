package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentor.domain.QMentor.mentor;
import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;
import static com.anchor.domain.mentoring.domain.QMentoringDetail.mentoringDetail;
import static com.anchor.domain.mentoring.domain.QMentoringTag.mentoringTag;
import static com.anchor.domain.user.domain.QUser.user;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
@Repository
public class QMentoringRepositoryImpl implements QMentoringRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final ObjectMapper objectMapper;

  private static NumberTemplate<Double> getScore(StringPath target, String keyword) {
    return numberTemplate(Double.class, "function('match_against', {0}, {1})",
        target, keyword);
  }

  /*
   * 조건에 맞는 멘토링을 검색한 결과 값을 가져옵니다.
   *
   * 1차 정렬 기준은 다음과 같습니다.
   * 기준 1. 입력받은 키워드와 멘토링 제목 및 내용이 일치하는 정도를 기준으로 내림차순
   * 기준 2. 아이디를 기준으로 내림차순 (= 최신 멘토링 등록순)
   *
   * 2차 정렬 기준은 다음과 같습니다.
   * 기준 1. 멘토링 신청자 수를 기준으로 내림차순
   *
   * (참고로 2차 정렬은 요청에 따라 달라집니다.)
   */
  public Page<MentoringSearchResult> findMentorings(List<String> tags, String keyword, Pageable pageable) {
    Searchable searchable = Searchable.of(keyword);
    List<Long> mentoringIds = jpaQueryFactory.select(mentoring.id)
        .from(mentoring)
        .where(
            (searchable.getCondition())
                .and(equalsWith(tags))
        )
        .orderBy(searchable.getSort())
        .offset(from(pageable))
        .limit(to(pageable))
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

  @Override
  public List<Mentoring> findPopularMentoringTags() {

    List<Long> mentoringIds = jpaQueryFactory.select(mentoring.id)
        .from(mentoring)
        .orderBy(mentoring.totalApplicationNumber.desc())
        .offset(0)
        .limit(10)
        .fetch();

    return jpaQueryFactory.selectFrom(mentoring)
        .join(mentoring.mentoringTags, mentoringTag)
        .fetchJoin()
        .where(mentoring.id.in(mentoringIds))
        .fetch();
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

  public Optional<Mentoring> findMentoringDetailInfo(Long id) {
    return Optional.ofNullable(jpaQueryFactory.selectFrom(mentoring)
        .join(mentoring.mentor, mentor)
        .fetchJoin()
        .join(mentoring.mentoringDetail, mentoringDetail)
        .fetchJoin()
        .leftJoin(mentoring.mentoringTags, mentoringTag)
        .fetchJoin()
        .join(mentor.user, user)
        .fetchJoin()
        .where(mentoring.id.eq(id))
        .fetchOne());
  }

  private long from(Pageable pageable) {
    return pageable.getOffset();
  }

  private long to(Pageable pageable) {
    return from(pageable) + pageable.getPageSize();
  }

  private BooleanBuilder equalsWith(List<String> tags) {
    BooleanBuilder builder = new BooleanBuilder();
    if (!CollectionUtils.isEmpty(tags)) {
      tags.stream()
          .map(this::equalTag)
          .forEach(builder::or);
    }
    return builder;
  }

  private BooleanExpression equalTag(String tag) {
    if (StringUtils.hasText(tag)) {
      return mentoring.mentoringTags.any().tag.eq(tag);
    }
    return Expressions.TRUE;
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

  private static class Searchable {

    private NumberExpression<Double> score;

    private static Searchable of(String keyword) {
      Searchable searchable = new Searchable();
      searchable.setScore(keyword);
      return searchable;
    }

    private void setScore(String keyword) {
      if (StringUtils.hasText(keyword)) {
        NumberTemplate<Double> titleScore = getScore(mentoring.title, keyword);
        NumberTemplate<Double> contentsScore = getScore(mentoring.mentoringDetail.contents, keyword);
        score = titleScore.add(contentsScore);
        return;
      }
      score = Expressions.asNumber(1.0);
    }

    private BooleanExpression getCondition() {
      return score.gt(0);
    }

    private OrderSpecifier[] getSort() {
      if (score.equals(Expressions.asNumber(1.0))) {
        return new OrderSpecifier[]{mentoring.id.desc()};
      }
      return new OrderSpecifier[]{score.desc(), mentoring.id.desc()};
    }

  }

}
