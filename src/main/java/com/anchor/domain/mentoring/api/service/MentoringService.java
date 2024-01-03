package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.service.response.MentoringApplicationResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponse;
import com.anchor.domain.mentoring.api.service.response.MentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringUnavailableTimeResponse;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailResponseDto;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringResponseDto;
import com.anchor.domain.mentoring.api.service.response.ApplicationUnavailableTime;
import com.anchor.domain.mentoring.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringDefaultInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringUnavailableTime;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringUnavailableTimeRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentoringService {

  private final MentoringRepository mentoringRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final MentoringUnavailableTimeRepository mentoringUnavailableTimeRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;

  @Transactional
  public MentoringCreateResult create(Long mentorId, MentoringBasicInfo mentoringBasicInfo) {
    Mentor mentor = getMentorById(mentorId);
    Mentoring mentoring = Mentoring.createMentoring(mentor, mentoringBasicInfo);
    Mentoring savedMentoring = mentoringRepository.save(mentoring);
    return new MentoringCreateResult(savedMentoring.getId());
  }

  @Transactional
  public MentoringEditResult edit(Long id, MentoringBasicInfo mentoringBasicInfo) {
    Mentoring mentoring = getMentoringById(id);
    mentoring.changeBasicInfo(mentoringBasicInfo);
    Mentoring savedMentoring = mentoringRepository.save(mentoring);
    return new MentoringEditResult(savedMentoring.getId());
  }

  @Transactional
  public MentoringDeleteResult delete(Long id) {
    Mentoring mentoring = getMentoringById(id);
    mentoringRepository.delete(mentoring);
    return new MentoringDeleteResult(mentoring.getId());
  }

  @Transactional
  public MentoringContentsEditResult editContents(Long id, MentoringContentsInfo mentoringContentsInfo) {
    Mentoring mentoring = getMentoringById(id);
    mentoring.editContents(mentoringContentsInfo);
    Mentoring savedMentoring = mentoringRepository.save(mentoring);
    return new MentoringContentsEditResult(savedMentoring.getId());
  }

  @Transactional
  public MentoringContents getContents(Long id) {
    Mentoring mentoring = getMentoringById(id);
    return new MentoringContents(mentoring.getContents(), mentoring.getTags());
  }

  /**
   * 현재 저장되어있는 모든 멘토링을 조회합니다.
   */
  @Transactional(readOnly = true)
  public List<MentoringDefaultInfo> loadMentoringList() {
    List<Mentoring> mentoringList = mentoringRepository.findAll();

    return mentoringList.stream()
        .map(MentoringDefaultInfo::new)
        .toList();
  }

  /**
   * 입력한 ID를 통해 멘토링 상세정보를 조회합니다.
   */
  @Transactional(readOnly = true)
  public MentoringDetailInfo loadMentoringDetail(Long id) {

    Mentoring findMentoring = getMentoringById(id);

    return new MentoringDetailInfo(findMentoring);
  }

  /**
   * 멘토링 신청페이지 조회시, 신청 불가능한 시간을 데이터베이스에서 조회합니다.
   */
  @Transactional(readOnly = true)
  public List<ApplicationUnavailableTime> loadMentoringUnavailableTime(Long id) {

    Mentoring findMentoring = getMentoringById(id);

    List<MentoringUnavailableTime> mentoringUnavailableTime = mentoringUnavailableTimeRepository.findByMentorId(
        findMentoring.getMentor()
            .getId());

    return mentoringUnavailableTime
        .isEmpty() ?
        null :
        mentoringUnavailableTime
            .stream()
            .map(ApplicationUnavailableTime::new)
            .toList();
  }

  /**
   * 멘토링 신청이 완료되면 멘토링 신청내역을 저장합니다.
   */
  @Transactional
  public AppliedMentoringInfo saveMentoringApplication(SessionUser sessionUser,
      Long mentoringId, MentoringApplicationTime applicationTime) {

    Mentoring findMentoring = getMentoringById(mentoringId);

    User loginUser = getUser(sessionUser);

    MentoringApplication saveMentoringApplication = MentoringApplication.builder()
        .mentoring(findMentoring)
        .user(loginUser)
        .mentoringApplicationTime(applicationTime)
        .build();

    MentoringApplication saveResult = mentoringApplicationRepository.save(saveMentoringApplication);

    saveMentoringUnavailableTime(saveResult, findMentoring);

    return new AppliedMentoringInfo(saveResult);
  }


  public void addApplicationTimeFromSession
      (List<ApplicationUnavailableTime> sessionList, MentoringApplicationTime applicationTime) {

    ApplicationUnavailableTime targetMentoringApplicationUnavailableTime = applicationTime.convertToMentoringUnavailableTimeResponse();

    if (!sessionList.contains(targetMentoringApplicationUnavailableTime)) {
      sessionList.add(targetMentoringApplicationUnavailableTime);
    }
  }

  public boolean removeApplicationTimeFromSession
      (List<ApplicationUnavailableTime> sessionList, MentoringApplicationTime applicationTime) {

    ApplicationUnavailableTime targetMentoringApplicationUnavailableTime = applicationTime.convertToMentoringUnavailableTimeResponse();

    return sessionList.remove(targetMentoringApplicationUnavailableTime);
  }

  private Mentor getMentorById(Long id) {
    Mentor mentor = mentorRepository.findById(id)
                                    .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
    return mentor;
  }

  private Mentoring getMentoringById(Long id) {
    return mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(id + "에 해당하는 멘토링이 존재하지 않습니다."));
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private void saveMentoringUnavailableTime(MentoringApplication mentoringApplication, Mentoring findMentoring) {

    MentoringUnavailableTime saveMentoringUnavailableTime =
        new MentoringUnavailableTime(mentoringApplication, findMentoring);

    mentoringUnavailableTimeRepository.save(saveMentoringUnavailableTime);
  }
}
