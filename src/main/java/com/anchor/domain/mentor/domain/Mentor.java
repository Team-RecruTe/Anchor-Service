package com.anchor.domain.mentor.domain;

import com.anchor.domain.mentor.api.controller.request.MentorContentsRequest;
import com.anchor.domain.mentor.api.controller.request.MentorInfoRequest;
import com.anchor.domain.mentor.api.controller.request.MentorRegisterInfo;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="mentor")
public class Mentor extends BaseEntity {

  @Column(name="company_email", length = 50, unique = true)
  private String companyEmail;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Career career;

  @Column(name="account_number", length = 40, nullable = false)
  private String accountNumber;

  @Column(name="account_name", length = 20, nullable = false)
  private String accountName;

  @Column(name="bank_name", length = 20, nullable = false)
  private String bankName;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
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

  @OneToMany(
      mappedBy = "mentor",
      cascade = CascadeType.ALL
  )
  private Set<MentorSchedule> mentorSchedule = new HashSet<>();

  @OneToMany(mappedBy = "mentor", cascade = CascadeType.ALL)
  private List<Payup> payups = new ArrayList<>();

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

  public static Mentor of(User user, MentorRegisterInfo registerInfo) {
    return Mentor.builder()
        .user(user)
        .companyEmail(registerInfo.getCompanyEmail())
        .career(registerInfo.getCareer())
        .accountNumber(registerInfo.getAccountNumber())
        .bankName(registerInfo.getBankName())
        .accountName(registerInfo.getAccountName())
        .build();
  }

  public void modify(MentorInfoRequest mentorInfoRequest) {
    this.career = mentorInfoRequest.getCareer();
    this.bankName = mentorInfoRequest.getBankName();
    this.accountNumber = mentorInfoRequest.getAccountNumber();
    this.accountName = mentorInfoRequest.getAccountName();
  }

  public void editContents(MentorContentsRequest mentorContentsRequest) {
    if (Objects.nonNull(mentorIntroduction)) {
      this.mentorIntroduction.editContents(mentorContentsRequest.getContents());
    } else {
      this.mentorIntroduction = MentorIntroduction.addContents(mentorContentsRequest.getContents());
    }
  }

}