package com.anchor.domain.mentor.domain.repository.custom;

import static com.anchor.domain.mentoring.domain.QMentoringUnavailableTime.mentoringUnavailableTime;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.global.util.type.DateTimeRange;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class QMentorRepositoryImpl implements QMentorRepository {

  private final JPAQueryFactory jpaQueryFactory;
  private final JdbcTemplate jdbcTemplate;

  @Override
  public List<MentoringUnavailableTime> findUnavailableTimes(Long mentorId) {
    return jpaQueryFactory.selectFrom(mentoringUnavailableTime)
        .where(mentoringUnavailableTime.mentorId.eq(mentorId))
        .stream()
        .toList();
  }

  @Override
  public void deleteUnavailableTimes(Long mentorId) {
    jpaQueryFactory.delete(mentoringUnavailableTime)
        .where(mentoringUnavailableTime.mentorId.eq(mentorId))
        .execute();
  }

  @Override
  public void saveUnavailableTimes(Long mentorId, List<DateTimeRange> unavailableTimes) {
    for (DateTimeRange unavailableTime : unavailableTimes) {
      jpaQueryFactory.insert(mentoringUnavailableTime)
          .columns(mentoringUnavailableTime.fromDateTime, mentoringUnavailableTime.toDateTime,
              mentoringUnavailableTime.mentorId)
          .values(unavailableTime.getFrom(), unavailableTime.getTo(), mentorId)
          .execute();
    }
  }

  @Override
  public void saveUnavailableTimesWithBatch(Long mentorId, List<DateTimeRange> unavailableTimes) {
    String sql = "insert into mentoring_unavailable_time (mentor_id, from_date_time, to_date_time) VALUES (?, ?, ?)";
    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        DateTimeRange range = unavailableTimes.get(i);
        ps.setLong(1, mentorId);
        ps.setObject(2, range.getFrom());
        ps.setObject(3, range.getTo());
      }

      @Override
      public int getBatchSize() {
        return unavailableTimes.size();
      }
    });
  }


}
