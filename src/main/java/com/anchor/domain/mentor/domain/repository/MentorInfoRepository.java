package com.anchor.domain.mentor.domain.repository;

import com.anchor.domain.mentor.domain.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MentorInfoRepository extends JpaRepository<Mentor, Long> {
  //CRUD

}
