package com.anchor.domain.mentoring.domain.repository;

import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentoringUnavailableTimeRepository extends
    JpaRepository<MentoringUnavailableTime, Long> {

}
