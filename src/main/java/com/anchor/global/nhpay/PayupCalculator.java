package com.anchor.global.nhpay;

import java.math.BigDecimal;
import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter(AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum PayupCalculator {
  TAX("Tax", BigDecimal.valueOf(0.033)),
  DEFAULT_CHARGE("Default Charge", BigDecimal.valueOf(0.07)),
  SILVER_CHARGE("Silver Charge", BigDecimal.valueOf(0.05)),
  GOLD_CHARGE("Gold Charge", BigDecimal.valueOf(0.03)),
  PG_CHARGE("PG Charge", BigDecimal.valueOf(0.1));

  private final String description;
  private final BigDecimal rate;

  private static Integer calculate(Integer amount, EnumSet<PayupCalculator> exclusions) {
    BigDecimal totalRate = BigDecimal.ONE.subtract(exclusions.stream()
        .map(PayupCalculator::getRate)
        .reduce(BigDecimal.ZERO, BigDecimal::add));
    return new BigDecimal(amount).multiply(totalRate)
        .intValue();
  }

  public static Integer calculateCharge(Integer amount, PayupCalculator exclusion) {
    return calculate(amount, EnumSet.of(TAX, PG_CHARGE, exclusion));
  }

}
