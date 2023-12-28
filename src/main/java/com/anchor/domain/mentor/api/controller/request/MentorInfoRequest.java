package com.anchor.domain.mentor.api.controller.request;


import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorInfoRequest {

  private Career career;
  private String bankName;
  private String accountNumber;
  private String accountName;

  @Builder
  public MentorInfoRequest(Career career,String bankName, String accountNumber,String accountName){
    this.career = career;
    this.bankName = bankName;
    this.accountNumber = accountNumber;
    this.accountName = accountName;
  }

  //DB에 등록 : dto -> entity
  public Mentor toEntity(){
    return Mentor.builder()
        .career(career)
        .bankName(bankName)
        .accountNumber(accountNumber)
        .accountName(accountName)
        .build();
  }

}
