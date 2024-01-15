package com.anchor.domain.mentor.domain.repository.custom;

import static com.anchor.domain.mentor.domain.QMentor.mentor;
import static com.anchor.domain.mentor.domain.QMentorSchedule.mentorSchedule;

import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.domain.MentorSchedule;
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
  public MentorOpenCloseTimes findScheduleById(Long mentorId) {
    List<MentorSchedule> mentorSchedules = jpaQueryFactory.selectFrom(mentorSchedule).
        where(mentor.id.eq(mentorId))
        .fetch();

    return MentorOpenCloseTimes.of(mentorSchedules);
  }

  @Override
  public void saveMentoSchedules(Long mentorId, MentorOpenCloseTimes mentorOpenCloseTimes) {
    String sql = "insert into mentor_schedule (mentor_id, open_time, close_time, active_status, day_of_week) VALUES (?, ?, ?, ?, ?)";
    List<MentorSchedule> mentorSchedules = MentorSchedule.of(mentorOpenCloseTimes);

    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
      @Override
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        MentorSchedule schedule = mentorSchedules.get(i);
        ps.setLong(1, mentorId);
        ps.setObject(2, schedule.getOpenTime());
        ps.setObject(3, schedule.getCloseTime());
        ps.setObject(4, schedule.getActiveStatus()
            .name());
        ps.setObject(5, schedule.getDayOfWeek()
            .name());
      }

      @Override
      public int getBatchSize() {
        return mentorSchedules.size();
      }
    });
  }

  @Override
  public void deleteAllSchedules(Long mentorId) {
    jpaQueryFactory.delete(mentorSchedule)
        .where(mentor.id.eq(mentorId))
        .execute();
  }

}
