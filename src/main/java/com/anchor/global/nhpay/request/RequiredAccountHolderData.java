package com.anchor.global.nhpay.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.util.BankCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequiredAccountHolderData implements RequiredPayupData {

  @JsonProperty("Header")
  private PayupRequestHeader header;

  @JsonProperty("Bncd")
  private String bankCode;

  @JsonProperty("Acno")
  private String accountNumber;

  private RequiredAccountHolderData(PayupRequestHeader header, String bankCode, String accountNumber) {
    this.header = header;
    this.bankCode = bankCode;
    this.accountNumber = accountNumber;
  }

  public static RequiredAccountHolderData of(String institutionCode, String accessToken, Mentor mentor) {
    PayupRequestHeader header = PayupRequestHeader.createAccountHolderRequestHeader(institutionCode, accessToken);
    String bankName = mentor.getBankName();
    BankCode bankCode = BankCode.find(bankName);
    return new RequiredAccountHolderData(header, bankCode.getCode(), mentor.getAccountNumber());
  }
}
