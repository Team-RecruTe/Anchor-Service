package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentor.domain.MentorIntroduction;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorInfoResponse {

  private Long id;
  private String companyEmail;
  private Career career;
  private String bankName;
  private String accountNumber;
  private String accountName;
  private MentorIntroduction mentorIntroduction;
  private List<Mentoring> mentorings = new ArrayList<>();
  private User user;


  public MentorInfoResponse(Mentor mentor){
    this.id = mentor.getId();
    this.companyEmail = mentor.getCompanyEmail();
    this.career = mentor.getCareer();
    this.bankName = mentor.getBankName();
    this.accountNumber = mentor.getAccountNumber();
    this.accountName = mentor.getAccountName();
    this.mentorIntroduction = mentor.getMentorIntroduction();
    this.mentorings = mentor.getMentorings();
    this.user = mentor.getUser();
  }

}