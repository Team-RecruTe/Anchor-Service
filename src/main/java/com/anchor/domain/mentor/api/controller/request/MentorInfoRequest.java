package com.anchor.domain.mentor.api.controller.request;


import com.anchor.domain.mentor.domain.Career;
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

}