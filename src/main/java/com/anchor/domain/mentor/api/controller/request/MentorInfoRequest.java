package com.anchor.domain.mentor.api.controller.request;


import com.anchor.domain.mentor.domain.Career;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorInfoRequest {

  @NotNull(message = "경력은 필수 입력입니다.")
  private Career career;

  @NotBlank(message = "은행명은 필수 입력입니다.")
  private String bankName;

  @NotBlank(message = "계좌번호는 필수 입력입니다.")
  @Size(min = 1, max = 20, message = "1 ~ 20 자리 사이의 계좌번호만 입력가능합니다.")
  @Pattern(regexp = "^\\d+$", message = "숫자만 입력 가능합니다.")
  private String accountNumber;

  @NotBlank(message = "예금주명은 필수 입력입니다.")
  private String accountName;

  @Builder
  public MentorInfoRequest(Career career, String bankName, String accountNumber, String accountName) {
    this.career = career;
    this.bankName = bankName;
    this.accountNumber = accountNumber;
    this.accountName = accountName;
  }

}