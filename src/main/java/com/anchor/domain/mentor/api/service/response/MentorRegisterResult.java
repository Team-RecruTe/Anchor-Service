package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Career;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentorRegisterResult {
  private String companyEmail;
  private Career career;
  private String accountNumber;
  private String bankName;
  private String accountName;
}
