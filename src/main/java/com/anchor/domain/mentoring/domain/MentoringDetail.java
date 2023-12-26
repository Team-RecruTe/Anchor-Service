package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringDetail extends BaseEntity {

  @Lob
  @Column(nullable = false)
  private String contents;

}
