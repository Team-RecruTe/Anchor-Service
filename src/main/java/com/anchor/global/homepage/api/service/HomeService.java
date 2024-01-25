package com.anchor.global.homepage.api.service;

import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.global.homepage.api.service.response.PopularTagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class HomeService {

  private final MentoringRepository mentoringRepository;

  @Transactional
  public PopularTagResponse getPopularTags() {
    List<Mentoring> result = mentoringRepository.findPopularMentoringTags();
    return new PopularTagResponse(result.stream()
        .flatMap(mentoring -> mentoring.getMentoringTags()
            .stream())
        .map(MentoringTag::getTag)
        .distinct()
        .sorted()
        .toList());
  }

  @Transactional
  public TopMentoring getTopMentorings() {
    List<MentoringSearchResult> mentoringRank = mentoringRepository.findTopMentorings();
    return new TopMentoring(mentoringRank);
  }

}