package com.anchor.domain.mentor.api.controller.request;

import com.anchor.domain.mentor.domain.Career;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MentorRegisterInfo {
  private String companyEmail;
  private Career career;
  private String accountNumber;
  private String bankName;
  private String accountName;
}
