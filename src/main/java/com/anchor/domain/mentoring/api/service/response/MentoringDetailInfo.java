package com.anchor.domain.mentoring.api.service.response;

import com.anchor.domain.mentoring.domain.Mentoring;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MentoringDetailInfo {

  private MentoringDetailSearchResult mentoringDetailSearchResult;
  private Set<String> searchTags;

  @Builder
  private MentoringDetailInfo(MentoringDetailSearchResult mentoringDetailSearchResult, Set<String> searchTags) {
    this.mentoringDetailSearchResult = mentoringDetailSearchResult;
    this.searchTags = searchTags;
  }

  public static MentoringDetailInfo of(MentoringDetailSearchResult mentoringDetailSearchResult,
      Set<String> searchTags) {
    return MentoringDetailInfo.builder()
        .mentoringDetailSearchResult(mentoringDetailSearchResult)
        .searchTags(searchTags)
        .build();
  }

  @Getter
  @NoArgsConstructor
  public static class MentoringDetailSearchResult {

    private Long mentoringId;
    private Long mentorId;
    private String mentorImage;
    private String title;
    private String durationTime;
    private String content;
    private String mentorNickname;
    private Integer cost;
    private List<String> tags;

    @Builder
    private MentoringDetailSearchResult(Long mentoringId, Long mentorId, String mentorImage, String title,
        String durationTime, String content,
        String mentorNickname,
        Integer cost,
        List<String> tags) {
      this.mentoringId = mentoringId;
      this.mentorId = mentorId;
      this.mentorImage = mentorImage;
      this.title = title;
      this.durationTime = durationTime;
      this.content = content;
      this.mentorNickname = mentorNickname;
      this.cost = cost;
      this.tags = tags;
    }

    public static MentoringDetailSearchResult of(Mentoring mentoring) {
      return MentoringDetailSearchResult.builder()
          .mentoringId(mentoring.getId())
          .mentorId(mentoring.getMentor()
              .getId())
          .mentorImage(mentoring.getMentor()
              .getUser()
              .getImage())
          .title(mentoring.getTitle())
          .content(mentoring.getContents())
          .durationTime(mentoring.getDurationTime())
          .mentorNickname(mentoring.getMentor()
              .getUser()
              .getNickname())
          .cost(mentoring.getCost())
          .tags(mentoring.getTags())
          .build();
    }
  }
}
