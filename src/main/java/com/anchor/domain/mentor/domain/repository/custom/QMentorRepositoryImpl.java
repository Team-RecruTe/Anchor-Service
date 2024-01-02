package com.anchor.domain.mentor.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringUnavailableTime.mentoringUnavailableTime;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentorRepositoryImpl implements QMentorRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public void removeByIdAndProgressTime(Long mentorId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    jpaQueryFactory.delete(mentoringUnavailableTime)
        .where(mentoringUnavailableTime.mentor.id.eq(mentorId))
        .where(mentoringUnavailableTime.fromDateTime.eq(startDateTime))
        .where(mentoringUnavailableTime.toDateTime.eq(endDateTime));
  }

}
