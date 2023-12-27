package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.domain.mentoring.api.controller.request.MentoringBasicInfo;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Mentoring extends BaseEntity {

  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 10, nullable = false)
  private String durationTime;

  @Column(nullable = false)
  private Integer cost;

  @Column(nullable = false, columnDefinition = "int default 0")
  private Integer totalApplicationNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @OneToOne(
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @JoinColumn(name = "mentoring_detail_id")
  private MentoringDetail mentoringDetail;

  @OneToMany(
      mappedBy = "mentoring",
      orphanRemoval = true
  )
  private List<MentoringTag> mentoringTag = new ArrayList<>();

  @OneToMany(
      mappedBy = "mentoring",
      orphanRemoval = true
  )
  private List<MentoringApplication> mentoringApplications = new ArrayList<>();

  @Builder
  private Mentoring(String title, String durationTime, Integer cost, Mentor mentor) {
    this.title = title;
    this.durationTime = durationTime;
    this.cost = cost;
    this.mentor = mentor;
  }

  public void changeBasicInfo(MentoringBasicInfo mentoringBasicInfo) {
    this.title = mentoringBasicInfo.getTitle();
    this.durationTime = mentoringBasicInfo.getDurationTime();
    this.cost = mentoringBasicInfo.getCost();
  }
}
