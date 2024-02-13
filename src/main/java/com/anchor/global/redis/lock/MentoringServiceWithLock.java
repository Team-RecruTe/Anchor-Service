package com.anchor.global.redis.lock;

import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.global.exception.type.entity.MentoringNotFoundException;
import com.anchor.global.exception.type.mentoring.DuplicateReservedException;
import com.anchor.global.redis.client.ApplicationLockClient;
import com.anchor.global.redis.client.ReservationTimeInfo;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentoringServiceWithLock {

  private final MentoringRepository mentoringRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final ApplicationLockClient applicationLockClient;

  @Transactional
  public Mentoring increaseTotalApplication(Long mentoringId) {
    Mentoring mentoring = mentoringRepository.findById(mentoringId)
        .orElseThrow(MentoringNotFoundException::new);
    mentoring.increaseTotalApplication();
    return mentoringRepository.save(mentoring);
  }

  public String getLock(Long mentorId, ReservationTimeInfo reservationTimeInfo) {
    String key = ApplicationLockClient.createKey(mentorId, reservationTimeInfo.getReservedTime());
    ReservationTimeInfo preReservation = applicationLockClient.findByKey(key);
    if (Objects.nonNull(preReservation) && !preReservation.isOwner(reservationTimeInfo.getEmail())) {
      throw new DuplicateReservedException();
    }
    List<MentoringApplication> mentoringApplications = mentoringApplicationRepository.findAllByMentorId(mentorId);
    List<DateTimeRange> unavailableTimes = mentoringApplications.stream()
        .map(application -> DateTimeRange.of(application.getStartDateTime(), application.getEndDateTime()))
        .toList();
    DateTimeRange reservationTime = reservationTimeInfo.getReservedTime();
    unavailableTimes.stream()
        .filter(unavailableTime -> unavailableTime.isDuration(reservationTime.getFrom()))
        .findFirst()
        .ifPresent(unavailableTime -> {
          throw new DuplicateReservedException();
        });
    applicationLockClient.save(key, reservationTimeInfo);
    return key;
  }

}