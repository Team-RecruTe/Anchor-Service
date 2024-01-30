package com.anchor.global.redis.lock;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.global.exception.type.entity.MentoringNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentoringServiceWithLock {

  private final MentoringRepository mentoringRepository;

  @Transactional
  public Mentoring increaseTotalApplication(Long mentoringId) {
    Mentoring mentoring = mentoringRepository.findById(mentoringId)
        .orElseThrow(MentoringNotFoundException::new);
    mentoring.increaseTotalApplication();
    return mentoringRepository.save(mentoring);
  }

}
