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
import com.anchor.global.auth.SessionUser;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.mail.MentoringMailMessage;
import com.anchor.global.payment.portone.request.RequiredPaymentCancelData;
import com.anchor.global.payment.portone.response.PaymentCancelResult;
import com.anchor.global.payment.portone.response.PaymentResult;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.PaymentClient;
import com.anchor.global.util.type.DateTimeRange;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.NoSuchElementException;
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
  private final MentorRepository mentorRepository;
  private final PayupRepository payupRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final PaymentClient paymentClient;
  private final AsyncMailSender asyncMailSender;

  @Transactional
  public void changeMentoringStatus(Long mentorId, List<RequiredMentoringStatusInfo> requiredMentoringStatusInfos) {
    Mentor mentor = getMentor(mentorId);
    List<MailMessage> mailMessages = requiredMentoringStatusInfos.stream()
        .map(requiredMentoringStatusInfo -> changeStatus(mentor.getId(), requiredMentoringStatusInfo))
        .toList();
    asyncMailSender.sendMails(mailMessages);
  }

  private Mentor getMentor(Long id) {
    Mentor mentor = mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
    return mentor;
  }

  private MailMessage changeStatus(Long mentorId,
      RequiredMentoringStatusInfo requiredMentoringStatusInfo) {
    MailMessage mailMessage = null;
    DateTimeRange mentoringReservedTime = requiredMentoringStatusInfo.getMentoringReservedTime();
    MentoringStatus mentoringStatus = requiredMentoringStatusInfo.getMentoringStatus();
    try {
      MentoringApplication mentoringApplication = getMentoringApplication(mentorId, mentoringReservedTime);
      Mentoring mentoring = mentoringApplication.getMentoring();
      User user = mentoringApplication.getUser();
      Mentor mentor = mentoring.getMentor();
      Payment payment = mentoringApplication.getPayment();
      mentoringApplication.changeStatus(mentoringStatus);
      cancelPayemntIfCancelled(mentoringStatus, payment);
      mentoringApplicationRepository.save(mentoringApplication);
      publishNotification(mentoringApplication, mentoring, mentoringStatus);
      mailMessage = createMailMessage(user, mentoring, mentor, mentoringReservedTime.getFrom(), mentoringStatus);
    } catch (NullPointerException | PersistenceException e) {
      log.info("Exception: {}", e);
    }
    return mailMessage;
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

  private void cancelPayemntIfCancelled(MentoringStatus status, Payment payment) {
    RequiredPaymentCancelData requiredPaymentCancelData = new RequiredPaymentCancelData(payment);
    Optional<PaymentResult> paymentCancelResult = paymentClient.request(status, requiredPaymentCancelData);
    paymentCancelResult.ifPresent(result -> {
      payment.editPaymentCancelStatus((PaymentCancelResult) result);
    });
  }

  private MentoringApplication getMentoringApplication(Long id, DateTimeRange mentoringReservedTime) {
    LocalDateTime startDateTime = mentoringReservedTime.getFrom();
    LocalDateTime endDateTime = mentoringReservedTime.getTo();
    try {
      return mentoringApplicationRepository.findByMentorIdAndProgressTime(id, startDateTime, endDateTime);
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

  @Transactional(readOnly = true)
  public MentorPayupResult getMentorPayupResult(LocalDateTime startMonth, LocalDateTime currentMonth,
      SessionUser sessionUser) {
//    Long mentorId = sessionUser.getMentorId();
    Long mentorId = 1L;
    DateTimeRange actualCalendarRange = DateTimeRange.of(
        setFirstDayOfMonth(startMonth),
        setFirstDayOfNextMonth(currentMonth)
    );
    List<PayupInfo> payupInfos = payupRepository.findAllByMonthRange(actualCalendarRange, mentorId);
    return MentorPayupResult.of(payupInfos);
  }

  private LocalDateTime setFirstDayOfMonth(LocalDateTime startMonth) {
    return startMonth.with(TemporalAdjusters.firstDayOfMonth())
        .truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime setFirstDayOfNextMonth(LocalDateTime currentMonth) {
    return currentMonth.with(TemporalAdjusters.firstDayOfNextMonth())
        .truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getFirstDayOfSixMonthAgo(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.firstDayOfMonth())
        .minusMonths(5L)
        .truncatedTo(ChronoUnit.DAYS);
  }

  private LocalDateTime getFirstDayOfNextMonth(LocalDateTime dateTime) {
    return dateTime.with(TemporalAdjusters.firstDayOfNextMonth())
        .truncatedTo(ChronoUnit.DAYS);
  }
}
