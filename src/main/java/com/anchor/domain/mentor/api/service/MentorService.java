package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.mentor.api.service.response.MentoringUnavailableTimes;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.NonUniqueResultException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentorRepository mentorRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Transactional
  public void setUnavailableTimes(Long id, List<DateTimeRange> unavailableTimes) {
    mentorRepository.deleteUnavailableTimes(id);
    mentorRepository.saveUnavailableTimesWithBatch(id, unavailableTimes);
  }

  @Transactional
  public void changeMentoringStatus(
      Long id, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    Mentor mentor = getMentor(id);
    mentor.getMentorings()
          .forEach(mentoring -> changeStatusAll(mentoring.getId(), requiredMentoringStatusInfos));
  }

  private Mentor getMentor(Long id) {
    Mentor mentor = mentorRepository.findById(id)
                                    .orElseThrow(
                                        () -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
    return mentor;
  }

  private void changeStatusAll(
      Long mentoringId, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    requiredMentoringStatusInfos.forEach(requiredMentoringStatusInfo -> {
      changeStatus(mentoringId, requiredMentoringStatusInfo);
    });
  }

  private void changeStatus(
      Long mentoringId, RequiredMentoringStatusInfo requiredMentoringStatusInfo) {
    DateTimeRange mentoringReservedTime = requiredMentoringStatusInfo.getMentoringReservedTime();
    MentoringStatus mentoringStatus = requiredMentoringStatusInfo.getMentoringStatus();
    try {
      MentoringApplication mentoringApplication = getMentoringApplication(
          mentoringId, mentoringReservedTime);
      mentoringApplication.changeStatus(mentoringStatus);
      mentoringApplicationRepository.save(mentoringApplication);
    } catch (NullPointerException | PersistenceException e) {
      log.warn("Exception: {}", e);
    }
  }

  private MentoringApplication getMentoringApplication(
      Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    try {
      return mentoringApplicationRepository.findByMentoringIdAndProgressTime(
          id, startDateTime, endDateTime);
    } catch (NonUniqueResultException e) {
      log.warn("Exception: {}", e);
      throw new RuntimeException(e);
    }
  }

  public MentoringUnavailableTimes getUnavailableTimes(Long mentorId) {
    List<MentoringUnavailableTime> unavailableTimes = mentorRepository.findUnavailableTimes(
        mentorId);
    List<MentoringApplication> reservedMentorings = mentoringApplicationRepository.findTimesByMentoringIdAndStatus(
        mentorId, MentoringStatus.APPROVAL,
        MentoringStatus.WAITING);

    return MentoringUnavailableTimes.of(unavailableTimes, reservedMentorings);
  }
}
