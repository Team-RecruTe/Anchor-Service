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
import com.anchor.domain.mentoring.api.controller.request.MentoringRatingInterface;
import com.anchor.domain.mentoring.api.controller.request.MentoringReviewInfoInterface;
import com.anchor.domain.mentoring.api.service.response.ApplicationTimeInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringContents;
import com.anchor.domain.mentoring.api.service.response.MentoringContentsEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringCreateResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDeleteResult;
import com.anchor.domain.mentoring.api.service.response.MentoringDetailInfo.MentoringDetailSearchResult;
import com.anchor.domain.mentoring.api.service.response.MentoringEditResult;
import com.anchor.domain.mentoring.api.service.response.MentoringOrderUid;
import com.anchor.domain.mentoring.api.service.response.MentoringPayConfirmInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringPaymentInfo;
import com.anchor.domain.mentoring.api.service.response.MentoringSearchResult;
import com.anchor.domain.mentoring.api.service.response.TopMentoring;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.MentoringDetail;
import com.anchor.domain.mentoring.domain.MentoringStatus;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringRepository;
import com.anchor.domain.mentoring.domain.repository.MentoringReviewRepository;
import com.anchor.domain.notification.domain.ReceiverType;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.mail.AsyncMailSender;
import com.anchor.global.mail.MailMessage;
import com.anchor.global.redis.client.ApplicationLockClient;
import com.anchor.global.redis.message.NotificationEvent;
import com.anchor.global.util.type.DateTimeRange;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
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
  private final PaymentRepository paymentRepository;
  private final MentorScheduleRepository mentorScheduleRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final MentoringReviewRepository mentoringReviewRepository;
  private final ApplicationLockClient applicationLockClient;
  private final PayNumberCreator payNumberCreator;
  private final AsyncMailSender asyncMailSender;

  public List<MentoringReviewInfoInterface> getMentoringReviews(Long mentoringId) {
    List<MentoringReviewInfoInterface> reviewList = mentoringReviewRepository.getReviewList(mentoringId);
    return reviewList;
  }

  public MentoringRatingInterface getMentoringRatings(Long mentoringId) {
    MentoringRatingInterface averageRatings = mentoringReviewRepository.getAverageRatings(mentoringId);
    return averageRatings;
  }

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
    return new MentoringContents(mentoring.getTitle(), mentoring.getContents(), mentoring.getTags());
  }

  @Transactional(readOnly = true)
  public List<String> getPopularMentoringTags() {
    List<Mentoring> mentoringList = mentoringRepository.findPopularMentoringTags();
    return mentoringList.stream()
        .flatMap(mentoring -> mentoring.getMentoringTags()
            .stream())
        .map(MentoringTag::getTag)
        .distinct()
        .sorted()
        .toList();
  }

  /**
   * 입력한 ID를 통해 멘토링 상세정보를 조회합니다.
   */
  @Transactional(readOnly = true)
  public MentoringDetailSearchResult getMentoringDetailInfo(Long id) {
    Mentoring findMentoring = mentoringRepository.findMentoringDetailInfo(id)
        .orElseThrow(() -> new NoSuchElementException(id + "에 해당하는 멘토링이 존재하지 않습니다."));
    return MentoringDetailSearchResult.of(findMentoring);
  }

  /**
   * 멘토의 활동시간과 이미 신청된 멘토링시간, 결제중인 멘토링시간을 조회합니다.
   */
  @Transactional(readOnly = true)
  public ApplicationTimeInfo getMentoringActiveTimes(Long id) {
    Mentor mentor = getMentoringById(id).getMentor();
    String pattern = ApplicationLockClient.createMatchPattern(mentor);
    List<DateTimeRange> paymentTimes = applicationLockClient.findAllByKeyword(pattern);
    List<MentoringApplication> mentoringApplications = mentoringApplicationRepository.findAllByMentorId(mentor.getId());
    List<MentorSchedule> mentorSchedules = mentorScheduleRepository.findMentorScheduleByMentorId(mentor.getId());
    return ApplicationTimeInfo.create(mentoringApplications, mentorSchedules, paymentTimes);
  }

  /**
   * 멘토링 결제페이지에 필요한 정보를 조회한 후 반환합니다.
   */
  @Transactional(readOnly = true)
  public MentoringPayConfirmInfo getMentoringConfirmInfo(Long id, MentoringApplicationTime applicationTime,
      SessionUser sessionUser) {
    User user = getUser(sessionUser);
    Mentoring mentoring = getMentoringById(id);
    return MentoringPayConfirmInfo.of(user, mentoring, applicationTime);
  }

  /**
   * 멘토링 결제에 필요한 정보를 생성합니다.
   */
  @Transactional(readOnly = true)
  public MentoringPaymentInfo createPaymentInfo(Long id, MentoringApplicationUserInfo userInfo,
      SessionUser sessionUser) {
    Mentoring mentoring = getMentoringById(id);
    Mentor mentor = mentoring.getMentor();
    String key = ApplicationLockClient.createKey(mentor, sessionUser);
    DateTimeRange myApplicationLockTime = applicationLockClient.findByKey(key);
    String merchantUid = createMerchantUid();
    String impCode = payNumberCreator.getImpCode();
    return MentoringPaymentInfo.of(mentoring, myApplicationLockTime, userInfo, merchantUid, impCode);
  }

  /**
   * 멘토링 신청이 완료되면 멘토링 신청내역을 저장합니다.
   */
  @Transactional
  public MentoringOrderUid saveMentoringApplication(SessionUser sessionUser,
      Long id, MentoringApplicationInfo applicationInfo) {
    Mentoring mentoring = getMentoringById(id);
    Mentor mentor = mentoring.getMentor();
    String key = ApplicationLockClient.createKey(mentor, sessionUser);
    DateTimeRange myApplicationLockTime = applicationLockClient.findByKey(key);
    applicationInfo.addApplicationTime(myApplicationLockTime);
    User user = getUser(sessionUser);
    Payment payment = new Payment(applicationInfo);
    MentoringApplication mentoringApplication = new MentoringApplication(applicationInfo, mentoring, payment,
        user);
    mentoringApplicationRepository.save(mentoringApplication);
    applicationLockClient.remove(key);
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
        .startDateTime(applicationInfo.getStartDateTime())
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
  public void lock(Long id, SessionUser sessionUser, MentoringApplicationTime applicationTime) {
    DateTimeRange dateTimeRange = applicationTime.convertDateTimeRange();
    Mentor mentor = getMentoringById(id).getMentor();
    String key = ApplicationLockClient.createKey(mentor, sessionUser);
    applicationLockClient.save(key, dateTimeRange);
  }

  /**
   * Redis에 저장되어있던 시간대를 삭제합니다.
   */
  public void unlock(Long id, SessionUser sessionUser) {
    Mentor mentor = getMentoringById(id).getMentor();
    String key = ApplicationLockClient.createKey(mentor, sessionUser);
    applicationLockClient.remove(key);
  }

  /**
   * 결제진행중인 시간 잠금 유효시간을 갱신합니다.
   */
  public boolean refresh(Long id, SessionUser sessionUser) {
    Mentor mentor = getMentoringById(id).getMentor();
    String key = ApplicationLockClient.createKey(mentor, sessionUser);
    try {
      applicationLockClient.refresh(key);
      return true;
    } catch (RuntimeException e) {
      return false;
    }
  }

  public void autoChangeStatus(DateTimeRange targetDateRange) {
    List<MentoringApplication> result = mentoringApplicationRepository.findAllByNotCompleteForWeek(targetDateRange);
    result.forEach(application -> application.changeStatus(MentoringStatus.COMPLETE));
    mentoringApplicationRepository.saveAll(result);
  }

  private Mentor getMentorById(Long id) {
    return mentorRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토 정보가 없습니다."));
  }

  private Mentoring getMentoringByIdAndMentor(Long id, Mentor mentor) {
    return mentoringRepository.findByIdAndMentor(id, mentor)
        .orElseThrow(() -> new NoSuchElementException("일치하는 멘토링 정보가 없습니다."));
  }

  private Mentoring getMentoringById(Long id) {
    return mentoringRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(id + "에 해당하는 멘토링이 존재하지 않습니다."));
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
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

  private String createMerchantUid() {
    String today = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    List<Payment> paymentList = paymentRepository.findPaymentListStartWithToday(today);
    return payNumberCreator.getMerchantUid(paymentList, today);
  }

}
