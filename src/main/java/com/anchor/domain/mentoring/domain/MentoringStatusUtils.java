package com.anchor.domain.mentoring.domain;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class MentoringStatusUtils {

  public static Set<MentoringStatus> mentoringStatusSet = new HashSet<>();

  static {
    mentoringStatusSet.add(MentoringStatus.WAITING);
    mentoringStatusSet.add(MentoringStatus.APPROVAL);
    mentoringStatusSet.add(MentoringStatus.COMPLETE);
    mentoringStatusSet.add(MentoringStatus.CANCELLED);
  }

  public static MentoringStatus of(String status) {
    return mentoringStatusSet.stream()
        .filter(mentoringStatus -> mentoringStatus.isEqualTo(status))
        .findAny()
        .orElseThrow(() -> new NoSuchElementException("일치하는 상태가 없습니다."));
  }
  
}
