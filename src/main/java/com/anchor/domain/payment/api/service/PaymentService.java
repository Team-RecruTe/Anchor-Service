package com.anchor.domain.payment.api.service;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.mentoring.domain.repository.MentoringApplicationRepository;
import com.anchor.domain.payment.api.controller.request.PaymentResultInfo;
import com.anchor.domain.payment.api.controller.request.RequiredPaymentInfo;
import com.anchor.domain.payment.api.controller.request.TokenRequest;
import com.anchor.domain.payment.api.service.response.PaidMentoringInfo;
import com.anchor.domain.payment.api.service.response.SinglePaymentData;
import com.anchor.domain.payment.api.service.response.SinglePaymentData.PaymentDataDetail;
import com.anchor.domain.payment.api.service.response.TokenData;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.PaymentRepository;
import com.anchor.domain.user.domain.User;
import com.anchor.domain.user.domain.repository.UserRepository;
import com.anchor.global.auth.SessionUser;
import com.anchor.global.util.ExternalApiClient;
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
      log.warn(e.getClass() + " :: " + e.getMessage());

      return null;
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

  private MentoringApplication getMentoringApplication(RequiredPaymentInfo requiredPaymentInfo, User user) {
    return mentoringApplicationRepository.findAppliedMentoringByTimeAndUserId(
            requiredPaymentInfo.getStartDateTime(), requiredPaymentInfo.getEndDateTime(), user.getId())
        .orElseThrow(() -> new NoSuchElementException("조건에 부합하는 멘토링 신청이력이 존재하지 않습니다."));
  }

  private User getUser(SessionUser sessionUser) {
    return userRepository.findByEmail(sessionUser.getEmail())
        .orElseThrow(() -> new NoSuchElementException(sessionUser.getEmail() + "에 해당하는 회원이 존재하지 않습니다."));
  }


}
