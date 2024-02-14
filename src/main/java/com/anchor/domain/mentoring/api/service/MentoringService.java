package com.anchor.domain.mentoring.api.service;

import static com.anchor.global.mail.MentoringMailTitle.APPLY_BY_MENTEE;

import com.anchor.domain.image.domain.Image;
import com.anchor.domain.image.domain.repository.ImageRepository;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorSchedule;
import com.anchor.domain.mentor.domain.repository.MentorRepository;
import com.anchor.domain.mentor.domain.repository.MentorScheduleRepository;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationUserInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.domain.mentoring.api.service.response.ApplicationTimeInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringOrderUid;
import com.anchor.domain.mentoring.api.service.response.MentoringPayConfirmInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringPaymentInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.api.service.response.PopularTag;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringReview;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.notification.domain.ReceiverType;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.exception.type.entity.MentorNotFoundException;
import com.anchor.global.exception.type.entity.MentoringNotFoundException;
import com.anchor.global.exception.type.entity.UserNotFoundException;
import com.anchor.global.exception.type.redis.ReservationTimeExpiredException;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.redis.client.ApplicationLockClient;
import com.anchor.global.redis.client.ReservationTimeInfo;
import com.anchor.global.redis.lock.RedisLockFacade;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.PaymentClient;
import com.anchor.global.util.type.DateTimeRange;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentoringService {

  private final ImageRepository imageRepository;
  private final MentoringRepository mentoringRepository;
  private final UserRepository userRepository;
  private final MentorRepository mentorRepository;
  private final MentorScheduleRepository mentorScheduleRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MentoringReviewRepository mentoringReviewRepository;
  private final PaymentClient paymentClient;
  private final RedisLockFacade redisLockFacade;
  private final ApplicationLockClient applicationLockClient;
  private final AsyncMailSender asyncMailSender;

  @Value("${payment.imp-code}")
  private String impCode;

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
    mapImagesWithMentoringDetail(mentoringContentsInfo, mentoring);
    Mentoring savedMentoring = mentoringRepository.save(mentoring);
    return new MentoringContentsEditResult(savedMentoring.getId());
  }

  private void mapImagesWithMentoringDetail(MentoringContentsInfo mentoringContentsInfo, Mentoring mentoring) {
    MentoringDetail mentoringDetail = mentoring.getMentoringDetail();
    List<Image> savedImages = imageRepository.findAllById(mentoringContentsInfo.getImageIds());
    Image.mapMentoringDetails(mentoringDetail, savedImages);
    imageRepository.saveAll(savedImages);
  }


  @Transactional(readOnly = true)
  public MentoringContents getContents(Long id, Long mentorId) {
    Mentor mentor = getMentorById(mentorId);
    Mentoring mentoring = getMentoringByIdAndMentor(id, mentor);
    return MentoringContents.of(mentoring);
  }

  /**
   * 입력한 ID를 통해 멘토링 상세정보를 조회합니다.
   */
  @Transactional(readOnly = true)
  public MentoringDetailInfo getMentoringDetailInfo(Long id) {
    Mentoring mentoring = mentoringRepository.findMentoringDetailInfo(id)
        .orElseThrow(MentoringNotFoundException::new);
    List<MentoringReview> mentoringReviews = mentoringReviewRepository.findAllByMentoringId(id);
    return MentoringDetailInfo.of(mentoring, mentoringReviews);
  }

  /**
   * 멘토의 활동시간과 이미 신청된 멘토링시간, 결제중인 멘토링시간을 조회합니다.
   */
  @Transactional(readOnly = true)
  public ApplicationTimeInfo getMentoringActiveTimes(Long id) {
    Mentor mentor = getMentoringById(id).getMentor();
    List<DateTimeRange> unavailableTimes = getUnavailableTimes(mentor);
    List<MentorSchedule> mentorSchedules = mentorScheduleRepository.findMentorScheduleByMentorId(mentor.getId());
    return ApplicationTimeInfo.create(unavailableTimes, mentorSchedules);
  }

  /**
   * 멘토링 결제페이지에 필요한 정보를 조회한 후 반환합니다. 해당 결제페이지에 대한 결제고유번호를 생성하고, 결제금액 사전등록 요청을 진행합니다.
   */
  @Transactional(readOnly = true)
  public MentoringPayConfirmInfo getMentoringConfirmInfo(Long id, MentoringApplicationTime applicationTime,
      SessionUser sessionUser) {
    User user = getUser(sessionUser);
    Mentoring mentoring = getMentoringById(id);
    paymentClient.preRegisterAmount(user.getEmail(), mentoring.getCost());
    return MentoringPayConfirmInfo.of(user, mentoring, applicationTime);
  }

  /**
   * 멘토링 결제에 필요한 정보를 생성합니다.
   */
  @Transactional(readOnly = true)
  public MentoringPaymentInfo createPaymentInfo(Long id, MentoringApplicationUserInfo userInfo, String merchantUid,
      String redisLockKey) {
    Mentoring mentoring = getMentoringById(id);
    ReservationTimeInfo reservationTimeInfo = Objects.requireNonNull(applicationLockClient.findByKey(redisLockKey),
        () -> {
          throw new ReservationTimeExpiredException();
        });
    return MentoringPaymentInfo.of(mentoring, reservationTimeInfo.getReservedTime(), userInfo, merchantUid, impCode);
  }

  /**
   * 멘토링 신청이 완료되면 멘토링 신청내역을 저장합니다.
   */
  @Transactional
  public MentoringOrderUid saveMentoringApplication(Long id, SessionUser sessionUser, String redisLockKey,
      MentoringApplicationInfo applicationInfo) {
    Mentoring mentoring = getMentoringById(id);
    Mentor mentor = mentoring.getMentor();
    User user = getUser(sessionUser);
    Payment payment = new Payment(applicationInfo);
    MentoringApplication mentoringApplication = new MentoringApplication(applicationInfo, mentoring, payment, user);
    mentoringApplicationRepository.save(mentoringApplication);
    applicationLockClient.remove(redisLockKey);
    publishNotification(mentoring, mentoringApplication.getMentoringStatus());
    sendMailToMentor(mentoring, mentor, user, applicationInfo);
    return new MentoringOrderUid(mentoringApplication);
  }

  private void sendMailToMentor(Mentoring mentoring, Mentor mentor, User user,
      MentoringApplicationInfo applicationInfo) {
    asyncMailSender.sendMail(MailMessage.mentoringMessageBuilder()
        .title(APPLY_BY_MENTEE.getTitle())
        .mentoringTitle(mentoring.getTitle())
        .receiverEmail(mentor.getCompanyEmail())
        .opponentEmail(user.getEmail())
        .opponentNickName(user.getNickname())
        .startDateTime(applicationInfo.getReservedTime()
            .getFrom())
        .receiverType(ReceiverType.TO_MENTOR)
        .build());
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

  /**
   * Redis에 결제진행중인 시간대를 저장합니다.
   */
  @Transactional
  public String lock(Long id, SessionUser sessionUser, MentoringApplicationTime applicationTime) {
    DateTimeRange dateTimeRange = applicationTime.convertDateTimeRange();
    Mentor mentor = getMentoringById(id).getMentor();
    ReservationTimeInfo reservationTimeInfo = ReservationTimeInfo.of(sessionUser, dateTimeRange);
    return redisLockFacade.lockApplicationTime(mentor.getId(), reservationTimeInfo);
  }

  /**
   * Redis에 저장되어있던 시간대를 삭제합니다.
   */
  public void unlock(String key) {
    applicationLockClient.remove(key);
  }

  /**
   * 결제진행중인 시간 잠금 유효시간을 갱신합니다.
   */
  public void refresh(String key, String email) {
    applicationLockClient.refresh(key, email);
  }

  @Transactional
  public void autoChangeStatus(DateTimeRange targetDateRange) {
    List<MentoringApplication> result = mentoringApplicationRepository.findAllByNotCompleteForWeek(targetDateRange);
    result.forEach(application -> application.changeStatus(MentoringStatus.COMPLETE));
    mentoringApplicationRepository.saveAll(result);
  }

  private Mentor getMentorById(Long id) {
    return mentorRepository.findById(id)
        .orElseThrow(MentorNotFoundException::new);
  }

  private Mentoring getMentoringByIdAndMentor(Long id, Mentor mentor) {
    return mentoringRepository.findByIdAndMentor(id, mentor)
        .orElseThrow(MentoringNotFoundException::new);
  }

  private Mentoring getMentoringById(Long id) {
    return mentoringRepository.findById(id)
        .orElseThrow(MentoringNotFoundException::new);
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(UserNotFoundException::new);
  }

  @Transactional(readOnly = true)
  public Page<MentoringSearchResult> getMentorings(List<String> tags, String keyword, Pageable pageable) {
    return mentoringRepository.findMentorings(tags, keyword, pageable);
  }

  @Cacheable(cacheNames = "topMentoring", key = "'topMentoring'")
  @Transactional(readOnly = true)
  public TopMentoring getTopMentorings() {
    List<MentoringSearchResult> topMentorings = mentoringRepository.findTopMentorings();
    return new TopMentoring(topMentorings);
  }

  @Cacheable(cacheNames = "topTag", key = "'topTag'")
  @Transactional(readOnly = true)
  public List<PopularTag> getPopularTags() {
    return mentoringRepository.findPopularTags();
  }

  private List<DateTimeRange> getUnavailableTimes(Mentor mentor) {
    String pattern = ApplicationLockClient.createMatchPattern(mentor);
    List<ReservationTimeInfo> reservedTimes = applicationLockClient.findAllByKeyword(pattern);
    List<DateTimeRange> paymentTimes = reservedTimes.stream()
        .map(ReservationTimeInfo::getReservedTime)
        .collect(Collectors.toList());
    List<MentoringApplication> mentoringApplications = mentoringApplicationRepository.findAllByMentorId(mentor.getId());
    mentoringApplications.stream()
        .map(application -> DateTimeRange.of(application.getStartDateTime(), application.getEndDateTime()))
        .forEach(paymentTimes::add);
    return paymentTimes;
  }

}
