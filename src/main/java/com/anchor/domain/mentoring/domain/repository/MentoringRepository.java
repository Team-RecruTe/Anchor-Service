package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.Mentoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentoringRepository extends JpaRepository<Mentoring, Long> {

}
