package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringUnavailableTimeRepository extends JpaRepository<MentoringUnavailableTime, Long> {

  List<MentoringUnavailableTime> findByMentorId(Long mentorId);

}
