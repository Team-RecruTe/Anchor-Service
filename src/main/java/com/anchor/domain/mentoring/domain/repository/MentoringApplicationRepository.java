package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringApplicationRepository extends JpaRepository<MentoringApplication, Long> {

  @Query(value = "select ma from MentoringApplication as ma "
      + "where ma.mentoring.id = :mentoringId "
      + "and ma.startDateTime = :startDateTime "
      + "and ma.endDateTime = :endDateTime")
  Optional<MentoringApplication> findByIdAndProgressTime(
      @Param("mentoringId") Long mentoringId,
      @Param("startDateTime") LocalDateTime startDateTime,
      @Param("endDateTime") LocalDateTime endDateTime
  );

}
