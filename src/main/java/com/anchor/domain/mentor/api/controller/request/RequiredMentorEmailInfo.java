package com.anchor.domain.mentor.api.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequiredMentorEmailInfo {

  @NotBlank(message = "이메일은 필수입력 입니다.")
  @Email(message = "올바르지 않은 이메일 형식입니다.")
  private String receiver;
  private String userEmailCode;

  public boolean isSameAs(String emailCode) {
    return userEmailCode.equals(emailCode);
  }
}
