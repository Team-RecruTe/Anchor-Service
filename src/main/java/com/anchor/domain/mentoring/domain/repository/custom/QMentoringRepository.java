package com.anchor.domain.mentoring.domain.repository.custom;

import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QMentoringRepository {

  Page<MentoringSearchResult> findMentorings(List<String> tags, String keyword, Pageable pageable);

  List<MentoringSearchResult> findTopMentorings();

  List<Mentoring> findPopularMentoringTags();

  Optional<Mentoring> findMentoringDetailInfo(Long id);
}
