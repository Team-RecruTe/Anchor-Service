package com.anchor.domain.user.api.service;

import static com.anchor.global.mail.MentoringMailTitle.CANCEL_BY_MENTEE;
import static com.anchor.global.mail.MentoringMailTitle.COMPLETE_BY_MENTEE;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.notification.domain.ReceiverType;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.PayupRepository;
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
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.MentoringMailMessage;
import com.anchor.global.payment.portone.request.RequiredPaymentCancelData;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.redis.lock.RedisLockFacade;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.PaymentClient;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
  private final PayupRepository payupRepository;
  private final PaymentClient paymentClient;
  private final RedisLockFacade redisLockFacade;
  private final AsyncMailSender asyncMailSender;

  @Transactional
  public void writeReview(SessionUser sessionUser, MentoringReviewInfo mentoringReviewInfo) {
    MentoringApplication mentoringApplication = getMentoringApplicationByTimeRange(sessionUser.getId(),
        mentoringReviewInfo.getTimeRange()
            .getFrom(), mentoringReviewInfo.getTimeRange()
            .getTo());
    MentoringReview dbMentoringReviewInsert = MentoringReview.builder()
        .ratings(mentoringReviewInfo.getRatings())
        .contents(mentoringReviewInfo.getContents())
        .mentoringApplication(mentoringApplication)
        .build();
    mentoringApplication.completedReview();
    mentoringReviewRepository.save(dbMentoringReviewInsert);
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
        .orElseThrow(() -> new RuntimeException("리뷰가 존재하지 않습니다."));
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
  public boolean changeAppliedMentoringStatus(SessionUser sessionUser, MentoringStatusInfo changeRequest) {
    List<RequiredMentoringStatusInfo> mentoringStatusList = changeRequest.getRequiredMentoringStatusInfos();
    List<MailMessage> mailMessages = mentoringStatusList.stream()
        .map(status -> {
          MailMessage mailMessage = null;
          try {
            User user = getUser(sessionUser);
            DateTimeRange range = status.getMentoringReservedTime();
            MentoringApplication mentoringApplication = getMentoringApplication(range.getFrom(), range.getTo(),
                user.getId());
            Mentoring mentoring = mentoringApplication.getMentoring();
            Mentor mentor = mentoring.getMentor();
            validateMentoringStatus(status);
            changeStatus(mentoringApplication, mentoring, status.getMentoringStatus());
            mailMessage = createMailMessage(user, mentoring, mentor, range.getFrom(), status.getMentoringStatus());
          } catch (NoSuchElementException | IllegalArgumentException e) {
            log.info(e.getMessage());
          }
          return mailMessage;
        })
        .toList();
    asyncMailSender.sendMails(mailMessages);
    return true;
  }

  private MentoringApplication getMentoringApplication(LocalDateTime startDateTime, LocalDateTime endDateTime,
      Long userId) {
    return mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(startDateTime, endDateTime, userId)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 신청이력이 존재하지 않습니다."));
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

  private void validateMentoringStatus(RequiredMentoringStatusInfo statusInfo) {
    MentoringStatus status = statusInfo.getMentoringStatus();
    if (status.equals(MentoringStatus.WAITING) || status.equals(MentoringStatus.APPROVAL)) {
      throw new IllegalArgumentException("변경하려는 상태가 'CANCELED' 또는 'COMPLETE'가 아닙니다.");
    }
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private MentoringApplication getMentoringApplicationByTimeRange(Long userId,
      LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return mentoringApplicationRepository.findByStartDateTimeAndEndDateTimeAndUserId(
            startDateTime, endDateTime, userId)
        .orElseThrow(() -> new RuntimeException("신청내역이 존재하지 않습니다."));
  }

  private void changeStatus(MentoringApplication mentoringApplication, Mentoring mentoring,
      MentoringStatus mentoringStatus) {
    mentoringApplication.changeStatus(mentoringStatus);
    processPayment(mentoringApplication, mentoringStatus);
    publishNotification(mentoring, mentoringStatus);
    mentoringApplicationRepository.save(mentoringApplication);
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

  private void processPayment(MentoringApplication mentoringApplication, MentoringStatus mentoringStatus) {
    switch (mentoringStatus) {
      case CANCELLED -> cancelPayment(mentoringApplication, mentoringStatus);
      case COMPLETE -> savePayup(mentoringApplication);
    }
  }

  private void cancelPayment(MentoringApplication application, MentoringStatus mentoringStatus) {
    Payment payment = application.getPayment();
    RequiredPaymentCancelData requiredPaymentCancelData = new RequiredPaymentCancelData(payment);
    Optional<PaymentResult> paymentCancelResult = paymentClient.request(mentoringStatus, requiredPaymentCancelData);
    paymentCancelResult.ifPresent(result -> payment.editPaymentCancelStatus((PaymentCancelResult) result));
  }

  private void savePayup(MentoringApplication application) {
    Long mentoringId = mentoringApplicationRepository.getMentoringId(application);
    Mentoring mentoring = redisLockFacade.increaseTotalApplication(mentoringId);
    Mentor mentor = mentoring.getMentor();
    Payment payment = application.getPayment();
    Payup payup = Payup.builder()
        .mentor(mentor)
        .payment(payment)
        .build();
    payupRepository.save(payup);
  }

}
