package com.anchor.domain.mentor.domain;

import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.controller.request.MentorIntroductionRequest;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mentor extends BaseEntity {

  @Column(length = 50, unique = true)
  private String companyEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Career career;

  @Column(length = 40, nullable = false)
  private String accountNumber;

  @Column(length = 20, nullable = false)
  private String accountName;

  @Column(length = 20, nullable = false)
  private String bankName;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_introduction_id")
  private MentorIntroduction mentorIntroduction;

  @OneToMany(
      mappedBy = "mentor",
      cascade = CascadeType.ALL
  )
  private List<Mentoring> mentorings = new ArrayList<>();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  private Mentor(String companyEmail, Career career, String accountNumber, String accountName, String bankName,
      User user) {
    this.companyEmail = companyEmail;
    this.career = career;
    this.accountNumber = accountNumber;
    this.accountName = accountName;
    this.bankName = bankName;
    this.user = user;
  }

  public void modify(MentorInfoRequest mentorInfoRequest) {
    this.career = mentorInfoRequest.getCareer();
    this.bankName = mentorInfoRequest.getBankName();
    this.accountNumber = mentorInfoRequest.getAccountNumber();
    this.accountName = mentorInfoRequest.getAccountName();
  }

  public void editInfo(MentorInfoRequest mentorInfoRequest) {
    this.career = mentorInfoRequest.getCareer();
    this.bankName = mentorInfoRequest.getBankName();
    this.accountNumber = mentorInfoRequest.getAccountNumber();
    this.accountName = mentorInfoRequest.getAccountName();
  }

  public void editContents(MentorIntroductionRequest mentorIntroductionRequest) {
    if (this.mentorIntroduction == null) {
      this.mentorIntroduction = MentorIntroduction.addContents(mentorIntroductionRequest.getContents());
    } else {
      this.mentorIntroduction.editContents(mentorIntroductionRequest.getContents());
    }
  }


}
