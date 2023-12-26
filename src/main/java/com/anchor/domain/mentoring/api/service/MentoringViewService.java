package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentoringViewService {

  private final MentoringRepository mentoringRepository;

  @Transactional(readOnly = true)
  public List<MentoringResponseDto> loadMentoringList() {
    List<Mentoring> mentoringList = mentoringRepository.findAll();

    return mentoringList.stream()
        .map(MentoringResponseDto::new)
        .toList();
  }

  @Transactional(readOnly = true)
  public MentoringDetailResponseDto loadMentoringDetail(Long id) throws NoSuchElementException {
    Mentoring findMentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(id + "에 해당하는 멘토링이 존재하지 않습니다."));
    return new MentoringDetailResponseDto(findMentoring);
  }
}
