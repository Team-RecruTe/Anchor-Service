package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfos.MentoringStatusInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.global.util.DateTimeRange;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentorRepository mentorRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Transactional
  public void setUnavailableTimes(Long id, List<DateTimeRange> unavailableTimes) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));

    List<MentoringUnavailableTime> mentoringUnavailableTimes = mentor.getMentoringUnavailableTimes();
    mentoringUnavailableTimes.addAll(MentoringUnavailableTime.of(unavailableTimes));

    mentorRepository.save(mentor);
  }

  @Transactional
  public void changeMentoringStatus(Long id, List<MentoringStatusInfo> mentoringStatusInfos) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));

    mentor.getMentorings()
        .forEach(mentoring -> changeStatus(mentoring, mentoringStatusInfos));
  }

  @Transactional
  protected void changeStatus(Mentoring mentoring, List<MentoringStatusInfo> mentoringStatusInfos) {
    mentoringStatusInfos.forEach(mentoringStatusInfo -> {
      DateTimeRange mentoringEstimatedTime = mentoringStatusInfo.getMentoringEstimatedTime();
      LocalDateTime startDateTime = mentoringEstimatedTime.getFrom();
      LocalDateTime endDateTime = mentoringEstimatedTime.getTo();
      MentoringStatus mentoringStatus = mentoringStatusInfo.getMentoringStatus();
      MentoringApplication mentoringApplication = mentoringApplicationRepository.findByIdAndProgressTime(
              mentoring.getId(),
              startDateTime, endDateTime)
          .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 없습니다."));
      mentoringApplication.changeStatus(mentoringStatus);
    });
  }
}
