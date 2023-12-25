package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MentoringUnavailableTime extends BaseEntity {

  @Column(nullable = false)
  LocalDateTime fromDateTime;

  @Column(nullable = false)
  LocalDateTime toDateTime;

}
