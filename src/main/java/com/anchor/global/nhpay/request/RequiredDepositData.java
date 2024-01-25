package com.anchor.global.nhpay.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.util.BankCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredDepositData implements RequiredPayupData {

  @JsonProperty("Header")
  private PayupRequestHeader header;

  @JsonProperty("Bncd")
  private String bankCode;

  @JsonProperty("Acno")
  private String accountNumber;

  @JsonProperty("Tram")
  private String amount;

  @JsonProperty("DractOtlt")
  private String withdrawalAccountInfo;

  @JsonProperty("MractOtlt")
  private String depositAccountInfo;

  private RequiredDepositData(PayupRequestHeader header, String bankCode, String accountNumber, String amount,
      String accountMessage) {
    this.header = header;
    this.bankCode = bankCode;
    this.accountNumber = accountNumber;
    this.amount = amount;
    this.withdrawalAccountInfo = accountMessage;
    this.depositAccountInfo = accountMessage;
  }

  public static RequiredDepositData of(String institutionCode, String accessToken, Mentor mentor, Integer totalAmount) {
    PayupRequestHeader header = PayupRequestHeader.createDepositRequestHeader(institutionCode, accessToken);
    BankCode bankCode = BankCode.find(mentor.getBankName());
    String accountMessage = "Anchor 멘토링 정산";
    return new RequiredDepositData(header, bankCode.getCode(), mentor.getAccountNumber(), String.valueOf(totalAmount),
        accountMessage);
  }
}
