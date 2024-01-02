package com.anchor.domain.mentor.api.service;

import static com.anchor.domain.mentoring.domain.MentoringStatus.CANCELLED;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfos.MentoringStatusInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.global.util.DateTimeRange;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentorRepository mentorRepository;
  private final MentoringRepository mentoringRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Transactional
  public void setUnavailableTimes(Long id, List<DateTimeRange> unavailableTimes) {
    Mentor mentor = getMentor(id);
    List<MentoringUnavailableTime> mentoringUnavailableTimes = mentor.getMentoringUnavailableTimes();
    mentoringUnavailableTimes.addAll(MentoringUnavailableTime.of(unavailableTimes));
    mentorRepository.save(mentor);
  }

  @Transactional
  public void changeMentoringStatus(Long id, List<MentoringStatusInfo> mentoringStatusInfos) {
    Mentor mentor = getMentor(id);
    mentor.getMentorings()
        .forEach(mentoring -> changeStatusAll(id, mentoring.getId(), mentoringStatusInfos));
  }

  private Mentor getMentor(Long id) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
    return mentor;
  }

  private void changeStatusAll(Long mentorId, Long mentoringId, List<MentoringStatusInfo> mentoringStatusInfos) {
    mentoringStatusInfos.forEach(mentoringStatusInfo -> {
      changeStatus(mentorId, mentoringId, mentoringStatusInfo);
    });
  }

  private void changeStatus(Long mentorId, Long mentoringId, MentoringStatusInfo mentoringStatusInfo) {
    DateTimeRange mentoringReservedTime = mentoringStatusInfo.getMentoringReservedTime();
    MentoringStatus mentoringStatus = mentoringStatusInfo.getMentoringStatus();
    try {
      MentoringApplication mentoringApplication = getMentoringApplication(mentoringId, mentoringReservedTime);
      mentoringApplication.changeStatus(mentoringStatus);
      mentoringApplicationRepository.save(mentoringApplication);
      if (mentoringStatus.equals(CANCELLED)) {
        deleteUnavailableTime(mentorId, mentoringReservedTime);
      }
    } catch (NoSuchElementException e) {
      log.warn("Exception: {}", e);
    }
  }

  private void deleteUnavailableTime(Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    mentorRepository.removeByIdAndProgressTime(id, startDateTime, endDateTime);
  }

  private MentoringApplication getMentoringApplication(Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    MentoringApplication mentoringApplication = mentoringApplicationRepository.findByIdAndProgressTime(
            id, startDateTime, endDateTime)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 없습니다."));
    return mentoringApplication;
  }
}
