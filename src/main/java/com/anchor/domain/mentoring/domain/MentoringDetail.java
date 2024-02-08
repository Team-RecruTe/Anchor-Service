package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="mentoring_detail")
public class MentoringDetail extends BaseEntity {

  @Column(nullable = false, columnDefinition = "mediumtext")
  private String contents;

  private MentoringDetail(String contents) {
    this.contents = contents;
  }

  public static MentoringDetail registerDetail(String contents) {
    return new MentoringDetail(contents);
  }

  public void editDetail(String contents) {
    this.contents = contents;
  }

}
