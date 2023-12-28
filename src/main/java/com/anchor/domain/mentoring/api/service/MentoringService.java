package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreationResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentoringService {

  private final MentoringRepository mentoringRepository;

  @Transactional
  public MentoringCreationResult create(Mentor mentor,
      MentoringBasicInfo mentoringBasicInfo) {
    Mentoring mentoring = Mentoring.builder()
        .title(mentoringBasicInfo.getTitle())
        .durationTime(mentoringBasicInfo.getDurationTime())
        .cost(mentoringBasicInfo.getCost())
        .mentor(mentor)
        .build();

    Mentoring createdMentoring = mentoringRepository.save(mentoring);

    return new MentoringCreationResult(createdMentoring.getId());
  }

  @Transactional
  public MentoringEditResult edit(Long id, MentoringBasicInfo mentoringBasicInfo) {
    Mentoring mentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링이 없습니다."));
    mentoring.changeBasicInfo(mentoringBasicInfo);
    Mentoring editedMentoring = mentoringRepository.save(mentoring);
    return MentoringEditResult.of(editedMentoring);
  }

  @Transactional
  public void delete(Long id) {
    Mentoring mentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링이 없습니다."));
    mentoringRepository.delete(mentoring);
  }


  @Transactional
  public void registerContents(Long id, MentoringContentsInfo mentoringContentsInfo) {
    Mentoring mentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링이 없습니다."));

    mentoring.registerDetail(mentoringContentsInfo);
    mentoringRepository.save(mentoring);
  }

  @Transactional
  public void editContents(Long id, MentoringContentsInfo mentoringContentsInfo) {
    Mentoring mentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링이 없습니다."));

    mentoring.editDetail(mentoringContentsInfo);
    mentoringRepository.save(mentoring);
  }

  public void changeMentoringStatus() {

  }

  @Transactional
  public MentoringContentsResult getMentoringDetail(Long id) {
    Mentoring mentoring = mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링이 없습니다."));

    return new MentoringContentsResult(mentoring.getMentoringDetail()
        .getContents());
  }

}
