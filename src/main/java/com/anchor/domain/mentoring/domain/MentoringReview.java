package com.anchor.domain.mentoring.domain;

import com.anchor.domain.user.api.controller.request.RequiredEditReview;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="mentoring_review")
public class MentoringReview extends BaseEntity {

  @Column(nullable = false, columnDefinition = "mediumtext")
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

  public void editReview(RequiredEditReview editReview) {
    this.contents = editReview.getContents();
    this.ratings = editReview.getRatings();
  }
}
