package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentorInfoService {

  private final MentorInfoRepository mentorsInfoRepository;

  @Transactional(readOnly = true) //select
  public MentorInfoResponse findMentors(Long id) {
    Mentor mentor = mentorsInfoRepository.findById(id)
        .orElseThrow(()-> new RuntimeException("멘토 정보 페이지를 조회하실 수 없습니다."));
    return new MentorInfoResponse(mentor);
  }

  @Transactional
  public void modifyMentorsInfo(Long id, MentorInfoRequest mentorInfoRequest) {
    Mentor mentor = mentorsInfoRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("해당 멘토를 찾을 수 없습니다."));
    mentor.editEssence(mentorInfoRequest);
    mentorsInfoRepository.save(mentor);
  }

  @Transactional
  public void deleteMentors(Long id) {
    mentorsInfoRepository.deleteById(id);
  }


}



