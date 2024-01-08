package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentor.domain.repository.MentorIntroductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentorIntroductionService {

  private final MentorIntroductionRepository mentorIntroductionRepository;

  @Transactional(readOnly = true) //select
  public MentorIntroductionResponse findMentors(Long id) {
    MentorIntroduction mentorIntroduction = mentorIntroductionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("멘토 소개글 페이지를 조회하실 수 없습니다."));

    return new MentorIntroductionResponse(mentorIntroduction);
  }

  @Transactional
  public void editContents(Long id, MentorIntroductionRequest mentorIntroductionRequest) {
    MentorIntroduction mentorIntroduction = mentorIntroductionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("멘토 소개글 페이지를 조회하실 수 없습니다."));

    mentorIntroduction.editContents(mentorIntroductionRequest.getContents());
    mentorIntroductionRepository.save(mentorIntroduction);
  }


}