package com.anchor.global.util;

import static lombok.AccessLevel.PRIVATE;

import com.anchor.domain.payment.domain.Payment;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import jodd.util.RandomString;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class CodeCreator {

  public static String getMerchantUid(List<Payment> paymentList, String today) {
    String merchantUid = createMerchantUid(today);
    while (isDuplicate(paymentList, merchantUid)) {
      merchantUid = createMerchantUid(today);
    }
    return merchantUid;
  }

  public static String createEmailAuthCode() {
    RandomString randomString = new RandomString();
    return randomString.randomAlphaNumeric(12);
  }

  private static boolean isDuplicate(List<Payment> payments, String merchantUid) {
    if (payments.isEmpty()) {
      return false;
    }
    for (Payment payment : payments) {
      String savedImpUid = payment.getMerchantUid();
      if (savedImpUid.equals(merchantUid)) {
        return true;
      }
    }
    return false;
  }

  private static String createMerchantUid(String today) {
    String randomDigit = String.format("%05d", ThreadLocalRandom.current()
        .nextInt(0, 10000));
    return "toss_" + today + randomDigit;
  }

}
