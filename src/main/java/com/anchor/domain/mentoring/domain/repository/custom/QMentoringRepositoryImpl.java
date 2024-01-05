package com.anchor.domain.mentoring.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoring.mentoring;

import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentoringRepositoryImpl implements QMentoringRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public Page<MentoringSearchResult> findMentorings(List<String> tags, String keyword, Pageable pageable) {
    List<Long> mentoringIds = jpaQueryFactory.select(mentoring.id)
        .from(mentoring)
        .where(
            containsTitle(keyword).or(containsContents(keyword))
                .and(equalTags(tags))
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Mentoring> result = jpaQueryFactory.selectFrom(mentoring)
        .innerJoin(mentoring.mentor)
        .fetchJoin()
        .innerJoin(mentoring.mentoringTags)
        .fetchJoin()
        .innerJoin(mentoring.mentor.user)
        .fetchJoin()
        .where(
            mentoring.id.in(mentoringIds)
        )
        .fetch();

    List<MentoringSearchResult> mentoringSearchResults = result.stream()
        .map(MentoringSearchResult::of)
        .toList();

    return new PageImpl<>(mentoringSearchResults, pageable, mentoringSearchResults.size());
  }

  private Predicate containsContents(String keyword) {
    if (keyword != null && !keyword.isBlank()) {
      return mentoring.mentoringDetail.contents.contains(keyword);
    }
    return null;
  }

  private BooleanExpression containsTitle(String keyword) {
    if (keyword != null && !keyword.isBlank()) {
      return mentoring.title.contains(keyword);
    }
    return null;
  }

  private BooleanBuilder equalTags(List<String> tags) {
    BooleanBuilder builder = new BooleanBuilder();
    tags.stream()
        .map(this::equalTag)
        .forEach(builder::or);
    return builder;
  }

  private BooleanExpression equalTag(String tag) {
    if (tag != null && !tag.isBlank()) {
      return mentoring.mentoringTags.any().tag.eq(tag);
    }
    return null;
  }

}
