package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.domain.mentoring.api.controller.request.MentoringContentsInfo;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mentoring extends BaseEntity {

  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 10, nullable = false)
  private String durationTime;

  @Column(nullable = false)
  private Integer cost;

  @Column(nullable = false, columnDefinition = "int default 0")
  private Integer totalApplicationNumber = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @OneToOne(
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL
  )
  @JoinColumn(name = "mentoring_detail_id")
  private MentoringDetail mentoringDetail;

  @OneToMany(
      mappedBy = "mentoring",
      cascade = CascadeType.ALL
  )
  private Set<MentoringTag> mentoringTags = new HashSet<>();

  @OneToMany(
      mappedBy = "mentoring"
  )
  private List<MentoringApplication> mentoringApplications = new ArrayList<>();

  @Builder
  private Mentoring(String title, String durationTime, Integer cost, Mentor mentor,
      MentoringDetail mentoringDetail) {
    this.title = title;
    this.durationTime = durationTime;
    this.cost = cost;
    this.mentor = mentor;
    this.mentoringDetail = mentoringDetail;
  }

  public static Mentoring createMentoring(Mentor mentor, MentoringBasicInfo mentoringBasicInfo) {
    Mentoring mentoring = Mentoring.builder()
        .title(mentoringBasicInfo.getTitle())
        .durationTime(mentoringBasicInfo.getDurationTime())
        .cost(mentoringBasicInfo.getCost())
        .mentor(mentor)
        .build();
    mentor.getMentorings()
        .add(mentoring);
    return mentoring;
  }

  public List<String> getTags() {
    return mentoringTags.stream()
        .map(MentoringTag::getTag)
        .toList();
  }

  public String getContents() {
    return mentoringDetail.getContents();
  }

  public void changeBasicInfo(MentoringBasicInfo mentoringBasicInfo) {
    this.title = mentoringBasicInfo.getTitle();
    this.durationTime = mentoringBasicInfo.getDurationTime();
    this.cost = mentoringBasicInfo.getCost();
  }

  public void editContents(MentoringContentsInfo mentoringContentsInfo) {
    editMentoringTags(mentoringContentsInfo.getTags());
    editDetails(mentoringContentsInfo);
  }

  private void editMentoringTags(List<String> tags) {
    Set<String> savedTags = this.mentoringTags.stream()
        .map(MentoringTag::getTag)
        .collect(Collectors.toSet());
    List<String> mutableTags = new ArrayList<>(tags);
    mutableTags.removeAll(savedTags);
    this.mentoringTags.addAll(mutableTags.stream()
        .map(tag -> new MentoringTag(tag, this))
        .collect(Collectors.toSet()));
  }

  private void editDetails(MentoringContentsInfo mentoringContentsInfo) {
    if (this.mentoringDetail == null) {
      this.mentoringDetail = MentoringDetail.registerDetail(mentoringContentsInfo.getContents());
    } else {
      this.mentoringDetail.editDetail(mentoringContentsInfo.getContents());
    }
  }

}
