package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.domain.Mentoring;
import com.anchor.domain.mentoring.domain.MentoringTag;
import com.anchor.domain.user.domain.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringSearchResult {

  private MentorInfo mentorInfo;
  private MentoringInfo mentoringInfo;

  @Builder
  private MentoringSearchResult(
      MentorInfo mentorInfo,
      MentoringInfo mentoringInfo
  ) {
    this.mentorInfo = mentorInfo;
    this.mentoringInfo = mentoringInfo;
  }

  public static MentoringSearchResult of(Mentoring mentoring) {
    Mentor mentor = mentoring.getMentor();
    User user = mentor.getUser();
    return MentoringSearchResult.builder()
        .mentorInfo(MentorInfo.of(mentor, user))
        .mentoringInfo(MentoringInfo.of(mentoring))
        .build();
  }

  @Getter
  @NoArgsConstructor
  static class MentorInfo {

    private Long id;
    private String mentorImage;
    private String mentorNickname;
    private String career;
    private String companyEmail;

    @Builder
    private MentorInfo(Long id, String mentorImage, String mentorNickname, String career, String companyEmail) {
      this.id = id;
      this.mentorImage = mentorImage;
      this.mentorNickname = mentorNickname;
      this.career = career;
      this.companyEmail = companyEmail;
    }

    public static MentorInfo of(Mentor mentor, User user) {
      return MentorInfo.builder()
          .id(mentor.getId())
          .career(mentor.getCareer()
              .getRangeOfYear())
          .mentorImage(user.getImage())
          .mentorNickname(user.getNickname())
          .companyEmail(mentor.getCompanyEmail())
          .build();
    }

  }

  @Getter
  @NoArgsConstructor
  static class MentoringInfo {

    private Long id;
    private String title;
    private String durationTime;
    private Integer cost;
    private Integer totalApplicationNumber;
    private List<String> mentoringTags;

    @Builder
    private MentoringInfo(Long id, String title, String durationTime, Integer cost, Integer totalApplicationNumber,
        List<String> mentoringTags) {
      this.id = id;
      this.title = title;
      this.durationTime = durationTime;
      this.cost = cost;
      this.totalApplicationNumber = totalApplicationNumber;
      this.mentoringTags = mentoringTags;
    }

    public static MentoringInfo of(Mentoring mentoring) {
      return MentoringInfo.builder()
          .id(mentoring.getId())
          .title(mentoring.getTitle())
          .durationTime(mentoring.getDurationTime())
          .totalApplicationNumber(mentoring.getTotalApplicationNumber())
          .mentoringTags(mentoring.getMentoringTags()
              .stream()
              .map(MentoringTag::getTag)
              .toList())
          .cost(mentoring.getCost())
          .build();
    }

  }

}