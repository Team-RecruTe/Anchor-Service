package com.anchor.domain.user.api.service;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringUnavailableTimeRepository;
import com.anchor.domain.user.api.controller.request.AppliedMentoringStatus;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final MentoringUnavailableTimeRepository mentoringUnavailableTimeRepository;

  @Transactional(readOnly = true)
  public List<AppliedMentoringInfo> loadAppliedMentoringList(SessionUser sessionUser) {

    User loginUser = userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(
            () -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));

    List<MentoringApplication> mentoringApplicationList = loginUser.getMentoringApplicationList();

    return mentoringApplicationList.isEmpty() ?
        null :
        mentoringApplicationList
            .stream()
            .map(AppliedMentoringInfo::new)
            .toList();
  }


  /**
   * 아직 결제기능을 개발하지 않아, 결제취소 프로세스를 추가하지 않았습니다.
   */
  @Transactional
  public boolean changeAppliedMentoringStatus(Long applicationId,
      AppliedMentoringStatus appliedMentoringStatus) {

    MentoringApplication findMentoringApplication =
        mentoringApplicationRepository.findById(applicationId)
            .orElseThrow(
                () -> new NoSuchElementException(applicationId + "에 해당하는 멘토링 신청내역이 존재하지 않습니다."));

    MentoringStatus changeStatus = appliedMentoringStatus.getStatus();

    switch (changeStatus) {
      case CANCELED, COMPLETE -> {

        findMentoringApplication.changeMentoringStatus(changeStatus);

        deleteMentoringUnavailableTime(findMentoringApplication, appliedMentoringStatus);

        MentoringApplication modifiedMentoringStatus = mentoringApplicationRepository.save(
            findMentoringApplication);

        return isChangedMentoringStatus(modifiedMentoringStatus, changeStatus);
      }
      default -> throw new IllegalArgumentException("잘못된 상태변경입니다.");
    }
  }


  private void deleteMentoringUnavailableTime(MentoringApplication mentoringApplication,
      AppliedMentoringStatus appliedMentoringStatus) {

    LocalDateTime targetStartDateTime = appliedMentoringStatus.getStartDateTime();
    LocalDateTime targetEndDateTime = appliedMentoringStatus.getEndDateTime();

    List<MentoringUnavailableTime> mentoringUnavailableTime = mentoringApplication.getMentoring()
        .getMentor()
        .getMentoringUnavailableTime();

    for (MentoringUnavailableTime unavailableTime : mentoringUnavailableTime) {

      LocalDateTime fromDateTime = unavailableTime.getFromDateTime();
      LocalDateTime toDateTime = unavailableTime.getToDateTime();

      if (fromDateTime.isEqual(targetStartDateTime) && toDateTime.isEqual(targetEndDateTime)) {

        mentoringUnavailableTimeRepository.delete(unavailableTime);
      }
    }
  }

  private boolean isChangedMentoringStatus(MentoringApplication modifiedMentoringStatus,
      MentoringStatus changeStatus) {

    return modifiedMentoringStatus.getMentoringStatus()
        .equals(changeStatus);
  }
}
