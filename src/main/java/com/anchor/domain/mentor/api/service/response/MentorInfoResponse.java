package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorInfoResponse {

  private String companyEmail;
  private Career career;
  private String bankName;
  private String accountNumber;
  private String accountName;

  //entity -> dto
  public MentorInfoResponse(Mentor mentor){
    this.companyEmail = mentor.getCompanyEmail();
    this.career = mentor.getCareer();
    this.bankName = mentor.getBankName();
    this.accountNumber = mentor.getAccountNumber();
    this.accountName = mentor.getAccountName();
  }

}
