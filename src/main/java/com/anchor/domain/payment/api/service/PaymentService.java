package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.payment.api.controller.request.ApiPaymentCancelData;
import com.anchor.domain.payment.api.controller.request.PaymentCancelInfo.RequiredMentoringCancelInfo;
import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.controller.request.RequiredPaymentInfo;
import com.anchor.domain.payment.api.controller.request.TokenRequest;
import com.anchor.domain.payment.api.service.response.PaidMentoringInfo;
import com.anchor.domain.payment.api.service.response.PaymentCancelData;
import com.anchor.domain.payment.api.service.response.PaymentCancelData.PaymentCancelDetail;
import com.anchor.domain.payment.api.service.response.SinglePaymentData;
import com.anchor.domain.payment.api.service.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.domain.payment.api.service.response.TokenData;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.UserRole;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.ExternalApiClient;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private static final String ACCESS_TOKEN_URL = "https://api.iamport.kr/users/getToken";
  private static final String CANCEL_PAYMENT_URL = "https://api.iamport.kr/payments/cancel";
  private static final String SUCCESS = "success";
  private static final String FAIL = "fail";

  private final ExternalApiClient apiClient;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final MentoringApplicationRepository mentoringApplicationRepository;

  @Value("${payment.imp-key}")
  private String impKey;
  @Value("${payment.imp-secret}")
  private String impSecret;

  public String validatePaymentResult(PaymentResultInfo paymentResultInfo) {
    String impUid = paymentResultInfo.getImpUid();

    String accessToken = getAccessToken();

    PaymentDataDetail singlePaymentData = getSinglePaymentData(impUid, accessToken);

    return paymentResultInfo.paymentDataValidation(singlePaymentData) ? SUCCESS : FAIL;
  }

  @Transactional
  public PaidMentoringInfo savePayment(RequiredPaymentInfo requiredPaymentInfo, SessionUser sessionUser) {
    try {

      User user = getUser(sessionUser);

      MentoringApplication appliedMentoring = getMentoringApplication(requiredPaymentInfo, user);

      Payment savedPayment = paymentRepository.save(new Payment(requiredPaymentInfo, appliedMentoring));

      return new PaidMentoringInfo(appliedMentoring, savedPayment);

    } catch (RuntimeException e) {
      log.warn(e.getClass()
          .getSimpleName() + " :: " + e.getMessage());

      return null;
    }
  }

  public boolean paymentCancelRequest(List<RequiredMentoringCancelInfo> mentoringCancelInfos, SessionUser sessionUser) {
    User user = getUser(sessionUser);

    UserRole role = user.getRole();
    try {

      switch (role) {
        case USER -> paymentCancelProcess(mentoringCancelInfos, user);

        case MENTOR -> paymentCancelProcess(mentoringCancelInfos);
        // 관리자가 멘토링 신청내역을 취소해야한다면, 다른 메서드에서 취소작업을 진행하도록 추후에 구현하겠습니다.
        case ADMIN -> throw new IllegalArgumentException("관리자는 타인의 멘토링을 취소할 수 없습니다.");
      }

      return true;
    } catch (RuntimeException e) {
      log.warn(e.getClass()
          .getSimpleName() + " :: " + e.getMessage());

      return false;
    }
  }

  private String getAccessToken() {
    TokenRequest tokenRequest = new TokenRequest(impKey, impSecret);

    ResponseEntity<TokenData> tokenResponseEntity = apiClient.getTokenDataEntity(tokenRequest, ACCESS_TOKEN_URL);

    TokenData tokenData = Objects.requireNonNullElse(tokenResponseEntity.getBody(), null);

    if (tokenData.statusCheck()) {

      return tokenData.getResponse()
          .getAccessToken();

    } else {
      throw new RuntimeException(tokenData.getMessage());
    }
  }

  private PaymentDataDetail getSinglePaymentData(String impUid, String accessToken) {
    String requestUrl = createPaymentDataRequestUrl(impUid);

    ResponseEntity<SinglePaymentData> paymentResponseEntity =
        apiClient.getSinglePaymentDataEntity(requestUrl, accessToken);

    SinglePaymentData paymentData = Objects.requireNonNullElse(paymentResponseEntity.getBody(), null);

    if (paymentData.statusCheck()) {

      return paymentData.getResponse();

    } else {
      throw new RuntimeException(paymentData.getMessage());
    }
  }

  private String createPaymentDataRequestUrl(String impUid) {
    return "https://api.iamport.kr/payments/" + impUid;
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }

  private MentoringApplication getMentoringApplication(RequiredPaymentInfo requiredPaymentInfo, User user) {

    MentoringApplication mentoringApplication = mentoringApplicationRepository.findAppliedMentoringByTimeAndUserId
            (requiredPaymentInfo.getStartDateTime(), requiredPaymentInfo.getEndDateTime(), user.getId())
        .orElseThrow(() -> new NoSuchElementException("조건에 부합하는 멘토링 신청이력이 존재하지 않습니다."));

    if (mentoringApplication.isExistPayment()) {
      throw new RuntimeException("이미 결제내역이 존재합니다.");
    }
    return mentoringApplication;
  }


  private MentoringApplication getMentoringApplication(RequiredMentoringCancelInfo mentoringCancelInfo, User user) {

    MentoringApplication mentoringApplication = mentoringApplicationRepository.findMentoringApplicationByTimeRangeAndUserId
            (mentoringCancelInfo.getStartDateTime(), mentoringCancelInfo.getEndDateTime(), user.getId())
        .orElseThrow(() -> new NoSuchElementException("조건에 부합하는 멘토링 신청이력이 존재하지 않습니다."));

    Payment payment = mentoringApplication.getPayment();
    if (payment.isCancelled()) {
      throw new RuntimeException(mentoringApplication.getId() + " 는 이미 취소된 결제내역입니다.");
    }

    return mentoringApplication;
  }

  private MentoringApplication getMentoringApplication(RequiredMentoringCancelInfo mentoringCancelInfo) {

    MentoringApplication mentoringApplication = mentoringApplicationRepository.findMentoringApplicationByMentoringId
            (mentoringCancelInfo.getStartDateTime(), mentoringCancelInfo.getEndDateTime(),
                mentoringCancelInfo.getMentoringId())
        .orElseThrow(() -> new NoSuchElementException("조건에 부합하는 멘토링 신청이력이 존재하지 않습니다."));

    Payment payment = mentoringApplication.getPayment();
    if (payment.isCancelled()) {
      throw new RuntimeException(mentoringApplication.getId() + " 는 이미 취소된 결제내역입니다.");
    }

    return mentoringApplication;
  }

  private void paymentCancelProcess(List<RequiredMentoringCancelInfo> mentoringCancelInfos,
      User user) {

    List<ApiPaymentCancelData> paymentCancelDataList = mentoringCancelInfos.stream()
        .map(info -> {
          MentoringApplication mentoringApplication = getMentoringApplication(info, user);

          return new ApiPaymentCancelData(mentoringApplication.getPayment(), info);
        })
        .toList();

    String accessToken = getAccessToken();

    paymentCancelDataList.forEach(cancelData -> {
      PaymentDataDetail singlePaymentData = getSinglePaymentData(cancelData.getImpUid(), accessToken);

      if (cancelData.paymentDataValidation(singlePaymentData)) {
        paymentCancel(cancelData, accessToken);
      } else {
        throw new RuntimeException("결제정보가 일치하지 않습니다.");
      }
    });
  }

  private void paymentCancelProcess(List<RequiredMentoringCancelInfo> mentoringCancelInfos) {

    List<ApiPaymentCancelData> paymentCancelDataList = mentoringCancelInfos.stream()
        .map(info -> {
          MentoringApplication mentoringApplication = getMentoringApplication(info);

          return new ApiPaymentCancelData(mentoringApplication.getPayment(), info);
        })
        .toList();

    String accessToken = getAccessToken();

    paymentCancelDataList.forEach(cancelData -> {
      PaymentDataDetail singlePaymentData = getSinglePaymentData(cancelData.getImpUid(), accessToken);

      if (cancelData.paymentDataValidation(singlePaymentData)) {
        paymentCancel(cancelData, accessToken);
      } else {
        throw new RuntimeException("결제정보가 일치하지 않습니다.");
      }
    });
  }

  private void paymentCancel(ApiPaymentCancelData cancelData, String accessToken) {

    ResponseEntity<PaymentCancelData> paymentCancelDataEntity =
        apiClient.getPaymentCancelData(cancelData, accessToken, CANCEL_PAYMENT_URL);

    PaymentCancelData paymentCancelData = Objects.requireNonNullElse(paymentCancelDataEntity.getBody(), null);

    if (paymentCancelData.statusCheck()) {

      PaymentCancelDetail cancelDetail = paymentCancelData.getResponse();

      cancelData.cancelResultValidation(cancelDetail);

    } else {
      throw new RuntimeException(paymentCancelData.getMessage());
    }
  }

}
