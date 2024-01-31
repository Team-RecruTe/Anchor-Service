package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentor.domain.Career;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;


@Getter
public class MentorRegisterInfo {

  @Email
  private String companyEmail;

  private Career career;

  @NotBlank
  private String accountNumber;

  private String bankName;

  @NotBlank
  private String accountName;

  @Builder
  public MentorRegisterInfo(String companyEmail, Career career, String accountNumber, String bankName,
      String accountName) {
    this.companyEmail = companyEmail;
    this.career = career;
    this.accountNumber = accountNumber;
    this.bankName = bankName;
    this.accountName = accountName;
  }

}
