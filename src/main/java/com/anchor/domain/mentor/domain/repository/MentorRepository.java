package com.anchor.domain.mentor.domain.repository;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.custom.QMentorRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long>, QMentorRepository {

  boolean existsMentorByCompanyEmail(String email);

}
