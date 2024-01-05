package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.custom.QMentoringApplicationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringApplicationRepository extends JpaRepository<MentoringApplication, Long>,
    QMentoringApplicationRepository {

}
