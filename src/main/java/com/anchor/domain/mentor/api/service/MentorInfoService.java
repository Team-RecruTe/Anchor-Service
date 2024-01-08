package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.response.MentorIntroductionResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentor.domain.repository.MentorInfoRepository;
import com.anchor.domain.mentor.domain.repository.MentorIntroductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentorInfoService {

  private final MentorInfoRepository mentorsInfoRepository;
  private final MentorIntroductionRepository mentorIntroductionRepository;

  @Transactional(readOnly = true) //select
  public MentorInfoResponse findMentor(Long mentorId) {
    Mentor mentor = mentorsInfoRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    return new MentorInfoResponse(mentor);
  }

  @Transactional
  public void editInfo(Long mentorId, MentorInfoRequest mentorInfoRequest) {
    Mentor mentor = mentorsInfoRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    mentor.editInfo(mentorInfoRequest);
    mentorsInfoRepository.save(mentor);
  }

  @Transactional
  public void deleteMentors(Long mentorId) {
    mentorsInfoRepository.deleteById(mentorId);
  }

  @Transactional
  public void editIntroduction(Long mentorId, MentorIntroductionRequest mentorIntroductionRequest) {
    Mentor mentor = mentorsInfoRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    mentor.editContents(mentorIntroductionRequest);
    mentorsInfoRepository.save(mentor);
  }

}



