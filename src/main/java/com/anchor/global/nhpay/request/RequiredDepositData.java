package com.anchor.global.nhpay.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.global.nhpay.PayupCalculator;
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

  public static RequiredDepositData of(PayupRequestHeader header, MentoringApplication application) {
    Mentor mentor = application.getMentoring()
        .getMentor();
    Payment payment = application.getPayment();
    BankCode bankCode = BankCode.find(mentor.getBankName());
    Integer amount = payment.getAmount();
    Integer payUpAmount = PayupCalculator.totalCalculate(amount);
    String accountMessage = "Anchor 멘토링 정산";
    return new RequiredDepositData(header, bankCode.getCode(), mentor.getAccountNumber(), String.valueOf(payUpAmount),
        accountMessage);
  }

  public static RequiredDepositData of(PayupRequestHeader header, Mentor mentor, Integer totalAmount) {
    BankCode bankCode = BankCode.find(mentor.getBankName());
    String accountMessage = "Anchor 멘토링 정산";
    return new RequiredDepositData(header, bankCode.getCode(), mentor.getAccountNumber(),
        String.valueOf(totalAmount), accountMessage);
  }
}
