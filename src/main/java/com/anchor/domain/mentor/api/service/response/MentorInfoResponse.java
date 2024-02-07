package com.anchor.domain.mentor.api.service.response;

import com.anchor.domain.mentor.domain.Career;
import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.user.domain.User;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentorInfoResponse {

  private MentorInfo mentorInfo;
  private List<MentoringInfo> mentoringInfos;
  private UserInfo userInfo;

  private MentorInfoResponse(MentorInfo mentorInfo, UserInfo userInfo, List<MentoringInfo> mentoringInfos) {
    this.mentorInfo = mentorInfo;
    this.mentoringInfos = mentoringInfos;
    this.userInfo = userInfo;
  }

  public static MentorInfoResponse of(Mentor mentor) {
    MentorInfo mentorInfo = new MentorInfo(mentor);
    UserInfo userInfo = new UserInfo(mentor.getUser());
    List<MentoringInfo> mentoringInfos = mentor.getMentorings()
        .stream()
        .map(MentoringInfo::new)
        .toList();
    return new MentorInfoResponse(mentorInfo, userInfo, mentoringInfos);
  }

  @Getter
  static class MentorInfo {

    private Long id;
    private String companyEmail;
    private Career career;
    private String bankName;
    private String accountNumber;
    private String accountName;
    private String mentorIntroduction;

    private MentorInfo(Mentor mentor) {
      this.id = mentor.getId();
      this.companyEmail = mentor.getCompanyEmail();
      this.career = mentor.getCareer();
      this.bankName = mentor.getBankName();
      this.accountNumber = mentor.getAccountNumber();
      this.accountName = mentor.getAccountName();
      this.mentorIntroduction = Objects.nonNull(mentor.getMentorIntroduction()) ? mentor.getMentorIntroduction()
          .getContents() : "";
    }
  }

  @Getter
  static class UserInfo {

    private String image;
    private String nickname;

    private UserInfo(User user) {
      this.image = user.getImage();
      this.nickname = user.getNickname();
    }

  }

  @Getter
  static class MentoringInfo {

    private Long id;
    private String title;
    private String durationTime;
    private Integer cost;

    private MentoringInfo(Mentoring mentoring) {
      this.id = mentoring.getId();
      this.title = mentoring.getTitle();
      this.durationTime = mentoring.getDurationTime();
      this.cost = mentoring.getCost();
    }

  }

}