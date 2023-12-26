package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentor.domain.Mentor;
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
import org.hibernate.annotations.DynamicInsert;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Entity
public class Mentoring extends BaseEntity {

  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 10, nullable = false)
  private String durationTime;

  @Column(nullable = false)
  private Integer cost;

  @Column(columnDefinition = "int default 0")
  private Integer totalApplicationNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_detail_id")
  private MentoringDetail mentoringDetail;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_unavailable_time_id")
  private MentoringUnavailableTime mentoringUnavailableTime;

  @OneToMany(mappedBy = "mentoring")
  private List<MentoringTag> mentoringTag = new ArrayList<>();

  @OneToMany(mappedBy = "mentoring")
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
}
