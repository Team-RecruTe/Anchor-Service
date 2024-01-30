package com.anchor.domain.mentor.api.service;

import static com.anchor.global.mail.MentoringMailTitle.APPROVE_BY_MENTOR;
import static com.anchor.global.mail.MentoringMailTitle.CANCEL_BY_MENTOR;

import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentor.api.controller.request.MentoringStatusInfo.RequiredMentoringStatusInfo;
import com.anchor.domain.mentor.api.service.response.AppliedMentoringSearchResult;
import com.anchor.domain.mentor.api.service.response.MentorOpenCloseTimes;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult;
import com.anchor.domain.mentor.api.service.response.MentorPayupResult.PayupInfo;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.notification.domain.ReceiverType;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PayupRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.ServiceException;
import com.anchor.global.exception.type.entity.MentorNotFoundException;
import com.anchor.global.exception.type.entity.MentoringApplicationNotUniqueException;
import com.anchor.global.exception.type.entity.UserNotFoundException;
import com.anchor.global.exception.type.mentor.DuplicateEmailException;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.MentoringMailMessage;
import com.anchor.global.payment.portone.request.RequiredPaymentCancelData;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.PaymentClient;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.NonUniqueResultException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class MentorService {

  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;
  private final PayupRepository payupRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PaymentClient paymentClient;
  private final AsyncMailSender asyncMailSender;

  @Transactional
  public Mentor register(MentorRegisterInfo mentorRegisterInfo, SessionUser sessionUser) {
    mentorRepository.findByCompanyEmail(mentorRegisterInfo.getCompanyEmail())
        .ifPresent(mentor -> {
          throw new DuplicateEmailException();
        });
    User user = getUser(sessionUser);
    Mentor mentor = Mentor.of(user, mentorRegisterInfo);
    mentorRepository.save(mentor);
    return mentor;
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

  @Transactional(readOnly = true)
  public Page<AppliedMentoringSearchResult> getMentoringApplications(Long mentorId, Pageable pageable) {
    return mentoringApplicationRepository.findAllByMentorId(mentorId, pageable);
  }

  @Transactional
  public void changeMentoringStatus(Long mentorId, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    Mentor mentor = getMentor(mentorId);
    List<MailMessage> mailMessages = requiredMentoringStatusInfos.stream()
        .map(requiredMentoringStatusInfo -> changeStatus(mentor.getId(), requiredMentoringStatusInfo))
        .toList();
    asyncMailSender.sendMails(mailMessages);
  }

  @Transactional(readOnly = true)
  public MentorPayupResult getMentorPayupResult(LocalDateTime startMonth, LocalDateTime currentMonth,
      SessionUser sessionUser) {
//    Long mentorId = sessionUser.getMentorId();
    Long mentorId = 1L;
    DateTimeRange actualCalendarRange = DateTimeRange.of(
        getFirstDayOfMonth(startMonth),
        getFirstDayOfNextMonth(currentMonth)
    );
    List<PayupInfo> payupInfos = payupRepository.findAllByMonthRange(actualCalendarRange, mentorId);
    return MentorPayupResult.of(payupInfos);
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(UserNotFoundException::new);
  }

  private Mentor getMentor(Long id) {
    return mentorRepository.findById(id)
        .orElseThrow(MentorNotFoundException::new);
  }

  private MailMessage changeStatus(Long mentorId, RequiredMentoringStatusInfo requiredMentoringStatusInfo) {
    MailMessage mailMessage = null;
    DateTimeRange mentoringReservedTime = requiredMentoringStatusInfo.getMentoringReservedTime();
    MentoringStatus mentoringStatus = requiredMentoringStatusInfo.getMentoringStatus();
    try {
      MentoringApplication mentoringApplication = getMentoringApplication(mentorId, mentoringReservedTime);
      Mentoring mentoring = mentoringApplication.getMentoring();
      User user = mentoringApplication.getUser();
      Mentor mentor = mentoring.getMentor();
      Payment payment = mentoringApplication.getPayment();
      cancelPayemntIfCancelled(mentoringStatus, payment);
      mentoringApplication.changeStatus(mentoringStatus);
      mentoringApplicationRepository.save(mentoringApplication);
      publishNotification(mentoringApplication, mentoring, mentoringStatus);
      mailMessage = createMailMessage(user, mentoring, mentor, mentoringReservedTime.getFrom(), mentoringStatus);
    } catch (ServiceException e) {
      log.warn("[멘토번호 :: {} || 멘토링시간 :: {}] 상태변경 실패", mentorId, mentoringReservedTime.toString());
    }
    return mailMessage;
  }

  private MentoringApplication getMentoringApplication(Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    try {
      return mentoringApplicationRepository.findByMentorIdAndProgressTime(id, startDateTime, endDateTime);
    } catch (NonUniqueResultException e) {
      throw new MentoringApplicationNotUniqueException(e);
    }
  }

  private void cancelPayemntIfCancelled(MentoringStatus status, Payment payment) {
    RequiredPaymentCancelData requiredPaymentCancelData = new RequiredPaymentCancelData(payment);
    Optional<PaymentResult> paymentCancelResult = paymentClient.request(status, requiredPaymentCancelData);
    paymentCancelResult.ifPresent(result -> payment.editPaymentCancelStatus((PaymentCancelResult) result));
  }

  private void publishNotification(MentoringApplication mentoringApplication, Mentoring mentoring,
      MentoringStatus mentoringStatus) {
    applicationEventPublisher.publishEvent(NotificationEvent.builder()
        .email(mentoringApplication.getUser()
            .getEmail())
        .mentoringId(mentoring.getId())
        .title(mentoring.getTitle())
        .mentoringStatus(mentoringStatus)
        .receiverType(ReceiverType.TO_MENTEE)
        .build());
  }

  private MentoringMailMessage createMailMessage(User user, Mentoring mentoring, Mentor mentor,
      LocalDateTime startDateTime, MentoringStatus status) {
    String title = switch (status) {
      case CANCELLED -> CANCEL_BY_MENTOR.getTitle();
      case APPROVAL -> APPROVE_BY_MENTOR.getTitle();
      default -> null;
    };

    return MailMessage.mentoringMessageBuilder()
        .title(title)
        .mentoringTitle(mentoring.getTitle())
        .receiverEmail(mentor.getCompanyEmail())
        .opponentEmail(user.getEmail())
        .opponentNickName(user.getNickname())
        .startDateTime(startDateTime)
        .receiverType(ReceiverType.TO_MENTEE)
        .build();
  }

  private LocalDateTime getFirstDayOfMonth(LocalDateTime startMonth) {
    return startMonth.with(TemporalAdjusters.firstDayOfMonth())
        .truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getFirstDayOfNextMonth(LocalDateTime currentMonth) {
    return currentMonth.with(TemporalAdjusters.firstDayOfNextMonth())
        .truncatedTo(ChronoUnit.DAYS);
  }
}
