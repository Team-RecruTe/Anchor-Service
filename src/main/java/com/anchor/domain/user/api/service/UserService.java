package com.anchor.domain.user.api.service;

import static com.anchor.global.mail.MentoringMailTitle.CANCEL_BY_MENTEE;
import static com.anchor.global.mail.MentoringMailTitle.COMPLETE_BY_MENTEE;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.mentoring.api.service.MentoringStatusChangeProcessor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.notification.domain.ReceiverType;
import com.anchor.domain.user.api.controller.request.MentoringReservedTime;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.user.api.controller.request.RequiredEditReview;
import com.anchor.domain.user.api.controller.request.UserImageRequest;
import com.anchor.domain.user.api.controller.request.UserNicknameRequest;
import com.anchor.domain.user.api.service.response.AppliedMentoringInfo;
import com.anchor.domain.user.api.service.response.UserInfoResponse;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.AnchorException;
import com.anchor.global.exception.type.entity.InvalidStatusException;
import com.anchor.global.exception.type.entity.MentoringApplicationNotFoundException;
import com.anchor.global.exception.type.entity.ReviewNotFoundException;
import com.anchor.global.exception.type.entity.UserNotFoundException;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.MentoringMailMessage;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MentoringReviewRepository mentoringReviewRepository;
  private final AsyncMailSender asyncMailSender;
  private final MentoringStatusChangeProcessor statusChangeProcessor;

  @Transactional
  public void writeReview(SessionUser sessionUser, MentoringReviewInfo mentoringReviewInfo) {
    MentoringApplication mentoringApplication = getMentoringApplicationByTimeRange(sessionUser.getId(),
        mentoringReviewInfo.getTimeRange()
            .getFrom(), mentoringReviewInfo.getTimeRange()
            .getTo());
    MentoringReview mentoringReview = MentoringReview.builder()
        .ratings(mentoringReviewInfo.getRatings())
        .contents(mentoringReviewInfo.getContents())
        .mentoringApplication(mentoringApplication)
        .build();
    mentoringApplication.completedReview();
    mentoringReviewRepository.save(mentoringReview);
  }

  @Transactional(readOnly = true)
  public RequiredEditReview getReview(SessionUser sessionUser, MentoringReservedTime reservedTime) {
    DateTimeRange dateTimeRange = DateTimeRange.of(reservedTime.getStartTime(), reservedTime.getEndTime());
    MentoringReview mentoringReview = mentoringReviewRepository.findByTimeRange(dateTimeRange, sessionUser.getId());
    return RequiredEditReview.of(mentoringReview);
  }

  @Transactional
  public void editReview(RequiredEditReview editReview) {
    MentoringReview mentoringReview = mentoringReviewRepository.findById(editReview.getId())
        .orElseThrow(ReviewNotFoundException::new);
    mentoringReview.editReview(editReview);
    mentoringReviewRepository.save(mentoringReview);
  }

  @Transactional(readOnly = true)
  public UserInfoResponse getProfile(SessionUser sessionUser) {
    User user = getUser(sessionUser);
    return new UserInfoResponse(user);
  }

  @Transactional
  public void editNickname(SessionUser sessionUser, UserNicknameRequest userNicknameRequest) {
    User user = getUser(sessionUser);
    user.editNickname(userNicknameRequest);
    userRepository.save(user);
  }

  @Transactional
  public void uploadImage(SessionUser sessionUser, UserImageRequest userImageRequest) {
    User user = getUser(sessionUser);
    user.uploadImage(userImageRequest);
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(SessionUser sessionUser) {
    User user = getUser(sessionUser);
    userRepository.delete(user);
  }

  @Transactional(readOnly = true)
  public Page<AppliedMentoringInfo> loadAppliedMentoringList(SessionUser sessionUser, Pageable pageable) {
    User user = getUser(sessionUser);
    return mentoringApplicationRepository.findByUserId(user.getId(), pageable);
  }

  @Transactional
  public boolean changeMentoringStatus(SessionUser sessionUser, MentoringStatusInfo changeRequest) {
    User user = getUser(sessionUser);
    LocalDateTime requestTime = changeRequest.getRequestTime();
    List<RequiredMentoringStatusInfo> mentoringStatusInfos = changeRequest.getRequiredMentoringStatusInfos();
    List<MailMessage> mailMessages = mentoringStatusInfos.stream()
        .map(statusInfo -> changeStatus(user, requestTime, statusInfo))
        .toList();
    asyncMailSender.sendMails(mailMessages);
    return true;
  }

  private MailMessage changeStatus(User user, LocalDateTime requestTime,
      RequiredMentoringStatusInfo requiredStatusInfo) {
    MailMessage mailMessage = null;
    DateTimeRange reservedTime = requiredStatusInfo.getMentoringReservedTime();
    MentoringApplication mentoringApplication = getMentoringApplication(user.getId(), reservedTime);
    Mentoring mentoring = mentoringApplication.getMentoring();
    Mentor mentor = mentoring.getMentor();
    MentoringStatus mentoringStatus = requiredStatusInfo.getMentoringStatus();
    try {
      validateMentoringStatus(mentoringStatus);
      statusChangeProcessor.changeStatusProcess(mentoringApplication, mentoringStatus, requestTime);
      mentoringApplicationRepository.save(mentoringApplication);
      publishNotification(mentoring, mentoringStatus);
      mailMessage = createMailMessage(user, mentoring, mentor, reservedTime.getFrom(), mentoringStatus);
    } catch (AnchorException | PersistenceException e) {
      log.warn("[회원번호 :: {} || 신청내역 번호 :: {}] 상태변경 실패", user.getId(), mentoringApplication.getId());
      log.warn("cause :: {}", e.getMessage());
    }
    return mailMessage;
  }

  private MentoringApplication getMentoringApplication(Long userId, DateTimeRange range) {
    return mentoringApplicationRepository.findByUserIdAndProgressTime(userId, range.getFrom(), range.getTo())
        .orElseThrow(MentoringApplicationNotFoundException::new);
  }

  private MentoringMailMessage createMailMessage(User user, Mentoring mentoring, Mentor mentor,
      LocalDateTime startDateTime, MentoringStatus status) {
    String title = switch (status) {
      case CANCELLED -> CANCEL_BY_MENTEE.getTitle();
      case COMPLETE -> COMPLETE_BY_MENTEE.getTitle();
      default -> null;
    };

    return MailMessage.mentoringMessageBuilder()
        .title(title)
        .mentoringTitle(mentoring.getTitle())
        .receiverEmail(mentor.getCompanyEmail())
        .opponentEmail(user.getEmail())
        .opponentNickName(user.getNickname())
        .startDateTime(startDateTime)
        .receiverType(ReceiverType.TO_MENTOR)
        .build();
  }

  private void validateMentoringStatus(MentoringStatus status) {
    if (status.equals(MentoringStatus.WAITING) || status.equals(MentoringStatus.APPROVAL)) {
      throw new InvalidStatusException();
    }
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(UserNotFoundException::new);
  }

  private MentoringApplication getMentoringApplicationByTimeRange(Long userId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(startDateTime, endDateTime, userId)
        .orElseThrow(MentoringApplicationNotFoundException::new);
  }

  private void publishNotification(Mentoring mentoring, MentoringStatus mentoringStatus) {
    applicationEventPublisher.publishEvent(NotificationEvent.builder()
        .email(mentoring.getMentor()
            .getCompanyEmail())
        .mentoringId(mentoring.getId())
        .title(mentoring.getTitle())
        .mentoringStatus(mentoringStatus)
        .receiverType(ReceiverType.TO_MENTOR)
        .build());
  }

}