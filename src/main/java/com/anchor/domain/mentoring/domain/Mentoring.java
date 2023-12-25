package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Mentoring extends BaseEntity {

  @Column(length = 50, nullable = false)
  private String title;

  @Column(length = 10, nullable = false)
  private String durationTime;

  @Column(nullable = false)
  private Integer cost;

  @Column(nullable = false, columnDefinition = "0")
  private Integer totalApplicationNumber;
  
}
