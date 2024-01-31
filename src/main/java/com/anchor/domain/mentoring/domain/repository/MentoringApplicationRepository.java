package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.custom.QMentoringApplicationRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringApplicationRepository extends JpaRepository<MentoringApplication, Long>,
    QMentoringApplicationRepository {

  Optional<MentoringApplication> findByStartDateTimeAndEndDateTimeAndUserId(LocalDateTime startDateTime,
      LocalDateTime endDateTime, Long userId);

}
