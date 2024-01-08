package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringTag;
import java.util.List;

public record MentoringDetailResponseDto(
    String title,
    String durationTime,
    String content,
    String nickname,
    Integer cost,
    List<String> tags
) {

  public MentoringDetailResponseDto(Mentoring mentoring) {
    this(mentoring.getTitle(),
        mentoring.getDurationTime(),
        mentoring.getMentoringDetail()
            .getContents(),
        mentoring.getMentor()
            .getUser()
            .getNickname(),
        mentoring.getCost(),
        mentoring.getMentoringTag()
            .stream()
            .map(MentoringTag::getTag)
            .toList());

  }
}
