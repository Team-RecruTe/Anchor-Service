package com.anchor.global.util;

import com.anchor.global.exception.type.api.BankNameNotFoundException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BankCode {

  HANA("하나은행", "001"),
  KDB("산업은행", "002"),
  IBK("기업은행", "003"),
  KB("국민은행", "004"),
  NH("농협은행", "011"),
  WOORI("우리은행", "020"),
  SC_KOREA("제일은행", "023"),
  CITY("시티은행", "027"),
  DAEGU("대구은행", "032"),
  KWANGJU("광주은행", "034"),
  JEJU("제주은행", "035"),
  JEONBUK("전북은행", "037"),
  BNK_KYONGNAM("경남은행", "039"),
  MG("새마을금고", "045"),
  SHINHAN("신한은행", "088"),
  KAKAO("카카오뱅크", "090");


  private static final Map<String, BankCode> BANK_CODES = Arrays.stream(BankCode.values())
      .collect(Collectors.toMap(BankCode::getBankName, Function.identity()));
  @Getter
  private final String bankName;
  @Getter
  private final String code;

  public static BankCode find(String bankName) {
    if (BANK_CODES.containsKey(bankName)) {
      return BANK_CODES.get(bankName);
    }
    throw new BankNameNotFoundException();
  }
}
