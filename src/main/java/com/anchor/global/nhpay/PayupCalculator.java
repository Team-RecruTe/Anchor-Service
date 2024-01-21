package com.anchor.global.nhpay;

import java.util.function.ToIntFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PayupCalculator {

  TAX(amount -> (int) (amount * 0.03)),
  DEFAULT_CHARGE(amount -> (int) (amount * 0.05)),
  PG_CHARGE(amount -> (int) (amount * 0.1));

  private final ToIntFunction<Integer> expression;

  public static Integer totalCalculate(Integer amount) {
    return amount - (TAX.calculate(amount) + DEFAULT_CHARGE.calculate(amount) + PG_CHARGE.calculate(amount));
  }

  public Integer calculate(Integer amount) {
    return expression.applyAsInt(amount);
  }
}
