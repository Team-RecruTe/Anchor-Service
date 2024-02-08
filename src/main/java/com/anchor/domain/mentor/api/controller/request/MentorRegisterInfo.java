package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentor.domain.Career;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;


@Getter
public class MentorRegisterInfo {

  @NotBlank(message = "이메일은 필수 입력입니다.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String companyEmail;

  @NotNull(message = "경력은 필수 입력입니다.")
  private Career career;

  @NotBlank(message = "계좌번호는 필수 입력입니다.")
  @Size(min = 1, max = 20, message = "1 ~ 20 자리 사이의 계좌번호만 입력가능합니다.")
  @Pattern(regexp = "^\\d+$", message = "숫자만 입력 가능합니다.")
  private String accountNumber;

  @NotBlank(message = "은행명은 필수 입력입니다.")
  private String bankName;

  @NotBlank(message = "예금주명은 필수 입력입니다.")
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
