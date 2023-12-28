package com.anchor.domain.mentor.api.service;

import com.anchor.domain.mentor.api.controller.request.MentoringUnavailableTimeInfos.DateTimeRange;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentorRepository mentorRepository;

  @Transactional
  public void setUnavailableTimes(Long id, List<DateTimeRange> unavailableTimes) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));

    List<MentoringUnavailableTime> mentoringUnavailableTimes = mentor.getMentoringUnavailableTimes();
    mentoringUnavailableTimes.addAll(MentoringUnavailableTime.of(unavailableTimes));

    mentorRepository.save(mentor);
  }

}
