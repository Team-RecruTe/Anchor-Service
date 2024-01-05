package com.anchor.domain.mentor.domain.repository;

import com.anchor.domain.mentor.domain.MentorIntroduction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorIntroductionRepository extends JpaRepository<MentorIntroduction, Long> {

}