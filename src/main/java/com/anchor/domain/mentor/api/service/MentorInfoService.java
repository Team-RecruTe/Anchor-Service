package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.api.service.response.MentorContentsEditResult;
import com.anchor.domain.mentor.api.service.response.MentorContents;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentorInfoService {

  private final MentorRepository mentorRepository;

  @Transactional
  public MentorInfoResponse findInfo(Long mentorId) {
    Mentor mentor = mentorRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    return new MentorInfoResponse(mentor);
  }

  @Transactional
  public void editInfo(Long mentorId, MentorInfoRequest mentorInfoRequest) {
    Mentor mentor = mentorRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    mentor.modify(mentorInfoRequest);
    mentorRepository.save(mentor);
  }

  @Transactional
  public void deleteMentors(Long mentorId) {
    mentorRepository.deleteById(mentorId);
  }

  @Transactional
  public MentorContentsEditResult editContents(Long mentorId, MentorContents mentorContents) {
    Mentor mentor = mentorRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    mentor.editContents(mentorContents);
    Mentor savedMentor = mentorRepository.save(mentor);
    return new MentorContentsEditResult(savedMentor.getId());
  }

  @Transactional(readOnly = true)
  public MentorContents getContents(Long mentorId, Long mentorIntroductionId){
    Mentor mentor = mentorRepository.findById(mentorId)
        .orElseThrow(()-> new RuntimeException("멘토를 찾을 수 없습니다."));
    Mentor mentorContents = mentorRepository.findByIdAndMentorIntroduction_Id(mentorId, mentorIntroductionId)
        .orElseThrow(()-> new RuntimeException("일치하는 멘토 소개글을 찾을 수 없습니다."));
    return new MentorContents(mentorContents.getMentorIntroduction().getContents());
  }

}