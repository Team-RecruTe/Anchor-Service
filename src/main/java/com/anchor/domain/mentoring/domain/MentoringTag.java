package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringTag extends BaseEntity {

  @Column(nullable = false, length = 30)
  private String tag;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_id")
  private Mentoring mentoring;

  @Builder
  private MentoringTag(String tag, Mentoring mentoring) {
    this.tag = tag;
    setMentoring(mentoring);
  }

  private void setMentoring(Mentoring mentoring) {
    this.mentoring = mentoring;
    mentoring.getMentoringTag()
        .add(this);
  }
}
