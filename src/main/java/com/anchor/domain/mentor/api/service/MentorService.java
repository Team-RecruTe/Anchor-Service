package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.NonUniqueResultException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentorRepository mentorRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Transactional
  public void changeMentoringStatus(Long id, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    Mentor mentor = getMentor(id);
    mentor.getMentorings()
        .forEach(mentoring -> changeStatusAll(mentoring.getId(), requiredMentoringStatusInfos));
  }

  private Mentor getMentor(Long id) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
    return mentor;
  }

  private void changeStatusAll(Long mentoringId, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    requiredMentoringStatusInfos.forEach(requiredMentoringStatusInfo -> {
      changeStatus(mentoringId, requiredMentoringStatusInfo);
    });
  }

  private void changeStatus(Long mentoringId, RequiredMentoringStatusInfo requiredMentoringStatusInfo) {
    DateTimeRange mentoringReservedTime = requiredMentoringStatusInfo.getMentoringReservedTime();
    MentoringStatus mentoringStatus = requiredMentoringStatusInfo.getMentoringStatus();
    try {
      MentoringApplication mentoringApplication = getMentoringApplication(mentoringId, mentoringReservedTime);
      mentoringApplication.changeStatus(mentoringStatus);
      mentoringApplicationRepository.save(mentoringApplication);
    } catch (NullPointerException | PersistenceException e) {
      log.warn("Exception: {}", e);
    }
  }

  private MentoringApplication getMentoringApplication(Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    try {
      return mentoringApplicationRepository.findByMentoringIdAndProgressTime(id, startDateTime, endDateTime);
    } catch (NonUniqueResultException e) {
      log.warn("Exception: {}", e);
      throw new RuntimeException(e);
    }
  }

  public Page<AppliedMentoringSearchResult> getMentoringApplications(Long mentorId, Pageable pageable) {
    return mentoringApplicationRepository.findAllByMentorId(mentorId,
        pageable);
  }

  public Mentor register(MentorRegisterInfo mentorRegisterInfo) {
    if (mentorRepository.findByCompanyEmail(mentorRegisterInfo.getCompanyEmail())
        .isPresent()) {
      throw new IllegalStateException("이미 존재하는 이메일");
    }
    Mentor dbInsertMentor = Mentor.builder()
        .companyEmail(mentorRegisterInfo.getCompanyEmail())
        .career(mentorRegisterInfo.getCareer())
        .accountNumber(mentorRegisterInfo.getAccountNumber())
        .bankName(mentorRegisterInfo.getBankName())
        .accountName(mentorRegisterInfo.getAccountName())
        .build();
    mentorRepository.save(dbInsertMentor);
    return dbInsertMentor;
  }

  @Transactional(readOnly = true)
  public MentorOpenCloseTimes getMentorSchedule(Long mentorId) {
    return mentorRepository.findScheduleById(mentorId);
  }

  @Transactional
  public void setMentorSchedule(Long mentorId, MentorOpenCloseTimes mentorOpenCloseTimes) {
    mentorRepository.deleteAllSchedules(mentorId);
    mentorRepository.saveMentoSchedules(mentorId, mentorOpenCloseTimes);
  }
}
