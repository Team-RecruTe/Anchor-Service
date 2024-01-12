package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.custom.QMentoringRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends JpaRepository<Mentoring, Long>, QMentoringRepository {

  Optional<Mentoring> findByIdAndMentor(Long id, Mentor mentor);

}