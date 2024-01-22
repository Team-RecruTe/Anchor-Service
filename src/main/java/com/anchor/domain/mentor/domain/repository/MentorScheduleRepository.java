package com.anchor.domain.mentor.domain.repository;

import com.anchor.domain.mentor.domain.MentorSchedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorScheduleRepository extends JpaRepository<MentorSchedule, Long> {

  List<MentorSchedule> findMentorScheduleByMentorId(Long mentorId);
}
