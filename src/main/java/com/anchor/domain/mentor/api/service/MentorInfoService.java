package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorContentsRequest;
import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.service.response.MentorContents;
import com.anchor.domain.mentor.api.service.response.MentorContentsEditResult;
import com.anchor.domain.mentor.api.service.response.MentorInfoResponse;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.global.auth.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentorInfoService {

  private final MentorRepository mentorRepository;

  @Transactional
  public MentorInfoResponse findInfo(Long mentorId) {
    Mentor mentor = getMentor(mentorId);
    return new MentorInfoResponse(mentor);
  }

  @Transactional
  public void editInfo(SessionUser sessionUser, MentorInfoRequest mentorInfoRequest) {
    Mentor mentor = getMentor(sessionUser.getMentorId());
    mentor.modify(mentorInfoRequest);
    mentorRepository.save(mentor);
  }

  @Transactional(readOnly = true)
  public MentorContents getContents(SessionUser sessionUser) {
    Mentor mentor = getMentor(sessionUser.getMentorId());
    return new MentorContents(mentor);
  }

  @Transactional
  public void deleteMentors(SessionUser sessionUser) {
    mentorRepository.deleteById(sessionUser.getMentorId());
  }

  @Transactional
  public MentorContentsEditResult editContents(SessionUser sessionUser, MentorContentsRequest mentorContents) {
    Mentor mentor = getMentor(sessionUser.getMentorId());
    mentor.editContents(mentorContents);
    Mentor savedMentor = mentorRepository.save(mentor);
    return new MentorContentsEditResult(savedMentor.getId());
  }

  private Mentor getMentor(Long mentorId) {
    return mentorRepository.findById(mentorId)
        .orElseThrow(() -> new RuntimeException("멘토를 찾을 수 없습니다."));
  }

}