package com.anchor.domain.mentoring.api.service;

import com.anchor.domain.payment.domain.Payment;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PayNumberCreator {

  private final Random random = new Random();

  @Getter
  @Value("${payment.imp-code}")
  private String impCode;

  public String getMerchantUid(List<Payment> paymentList, String today) {
    String merchantUid = generateMerchantUid(today);
    while (isDuplicate(paymentList, merchantUid)) {
      merchantUid = generateMerchantUid(today);
    }
    return merchantUid;
  }

  private boolean isDuplicate(List<Payment> payments, String impUid) {
    if (payments.isEmpty()) {
      return false;
    }
    for (Payment payment : payments) {
      String savedImpUid = payment.getImpUid();
      if (savedImpUid.equals(impUid)) {
        return true;
      }
    }
    return false;
  }

  private String generateMerchantUid(String today) {
    String randomDigit = String.format("%05d", random.nextInt(10000));
    return "toss_" + today + randomDigit;
  }

}
