package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringReview extends BaseEntity {

  @Lob
  @Column(nullable = false)
  private String contents;

  @Column(nullable = false)
  private Integer ratings;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_application_id")
  private MentoringApplication mentoringApplication;

  @Builder
  MentoringReview(String contents, Integer ratings, MentoringApplication mentoringApplication) {
    this.contents = contents;
    this.ratings = ratings;
    this.mentoringApplication = mentoringApplication;
  }
}
