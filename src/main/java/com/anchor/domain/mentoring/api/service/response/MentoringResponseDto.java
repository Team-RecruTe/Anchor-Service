package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;

public record MentoringResponseDto(
    String title,
    String nickname,
    String durationTime,
    Integer cost
) {

  public MentoringResponseDto(Mentoring mentoring) {
    this(mentoring.getTitle(),
        mentoring.getMentor()
            .getUser()
            .getNickname(),
        mentoring.getDurationTime(),
        mentoring.getCost());
  }
}
