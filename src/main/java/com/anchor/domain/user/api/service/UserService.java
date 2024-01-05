package com.anchor.domain.user.api.service;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Transactional(readOnly = true)
  public List<AppliedMentoringInfo> loadAppliedMentoringList(SessionUser sessionUser) {

    User user = getUser(sessionUser);

    List<MentoringApplication> mentoringApplicationList = user.getMentoringApplicationList();

    return mentoringApplicationList.isEmpty() ?
        null :
        mentoringApplicationList
            .stream()
            .map(AppliedMentoringInfo::new)
            .toList();
  }


  @Transactional
  public boolean changeAppliedMentoringStatus(SessionUser sessionUser, MentoringStatusInfo changeRequest) {
    User user = getUser(sessionUser);

    List<RequiredMentoringStatusInfo> mentoringStatusList = changeRequest.getMentoringStatusList();
    mentoringStatusList.forEach(status -> {
      try {

        if (status.mentoringStatusIsCanceledOrComplete()) {
          changeStatus(user, status);
        }

      } catch (NoSuchElementException | IllegalArgumentException e) {
        log.warn(e.getMessage());
      }
    });

    return true;
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private void changeStatus(User user, RequiredMentoringStatusInfo mentoringStatus) {
    LocalDateTime startDateTime = mentoringStatus.getStartDateTime();
    LocalDateTime endDateTime = mentoringStatus.getEndDateTime();
    Long userId = user.getId();

    MentoringApplication mentoringApplication =
        mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(startDateTime, endDateTime, userId)
            .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 존재하지 않습니다."));

    mentoringApplication.changeStatus(mentoringStatus.getStatus());

    mentoringApplicationRepository.save(mentoringApplication);
  }


}
