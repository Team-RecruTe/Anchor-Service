package com.anchor.global.nhpay.request;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.util.BankCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PayupRequestHeaderCreator {

  @Value("${payup.access-token}")
  private String accessToken;
  @Value("${payup.iscd}")
  private String institutionCode;

  public String createAccountRequestUrl(Mentor mentor) {
    return isNHBank(mentor.getBankName()) ?
        NHRequestUrl.NH_ACCOUNT_HOLDER_URI.getUrl() :
        NHRequestUrl.OTHER_ACCOUNT_HOLDER_URI.getUrl();
  }

  public String createDepositRequestUrl(Mentor mentor) {
    return isNHBank(mentor.getBankName()) ?
        NHRequestUrl.NH_DEPOSIT_URI.getUrl() :
        NHRequestUrl.OTHER_DEPOSIT_URI.getUrl();
  }

  public PayupRequestHeader createAccountRequestHeader(Mentor mentor) {
    return isNHBank(mentor.getBankName()) ? createAccountHolderHeader() : createOtherAccountHolderHeader();
  }

  public PayupRequestHeader createDepositRequestHeader(Mentor mentor) {
    return isNHBank(mentor.getBankName()) ? createDepositHeader() : createOtherDepositHeader();
  }

  private boolean isNHBank(String bankName) {
    return bankName.equals(BankCode.NH.getBankName());
  }

  private PayupRequestHeader createAccountHolderHeader() {
    return PayupRequestHeader.builder()
        .apiName(NHHeaders.NH_ACCOUNT_HOLDER_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.ACCOUNT_HOLDER_API_CODE.getValue())
        .accessToken(accessToken)
        .build();
  }

  private PayupRequestHeader createOtherAccountHolderHeader() {
    return PayupRequestHeader.builder()
        .apiName(NHHeaders.OTHER_ACCOUNT_HOLDER_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.ACCOUNT_HOLDER_API_CODE.getValue())
        .accessToken(accessToken)
        .build();
  }

  private PayupRequestHeader createDepositHeader() {
    return PayupRequestHeader.builder()
        .apiName(NHHeaders.NH_DEPOSIT_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.DEPOSIT_API_CODE.getValue())
        .accessToken(accessToken)
        .build();
  }

  private PayupRequestHeader createOtherDepositHeader() {
    return PayupRequestHeader.builder()
        .apiName(NHHeaders.OTHER_DEPOSIT_API_NAME.getValue())
        .institutionCode(institutionCode)
        .fintechApsno(NHHeaders.FINTECH_APS_NO.getValue())
        .apiServiceCode(NHHeaders.DEPOSIT_API_CODE.getValue())
        .accessToken(accessToken)
        .build();
  }

}
