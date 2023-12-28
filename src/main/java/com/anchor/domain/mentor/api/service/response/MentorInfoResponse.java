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
  private MentorIntroduction mentorIntroduction;

  //DB를 조회 : entity -> dto
  public MentorInfoResponse(Mentor entity){
    this.companyEmail = entity.getCompanyEmail();
    this.career = entity.getCareer();
    this.bankName = entity.getBankName();
    this.accountNumber = entity.getAccountNumber();
    this.accountName = entity.getAccountName();
    this.mentorIntroduction = entity.getMentorIntroduction();
  }

}
