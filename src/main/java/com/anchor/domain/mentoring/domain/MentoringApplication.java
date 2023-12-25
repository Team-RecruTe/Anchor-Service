package com.anchor.domain.mentoring.domain;

import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class MentoringApplication extends BaseEntity {

  @Column(nullable = false)
  LocalDateTime startDateTime;

  @Column(nullable = false)
  LocalDateTime endDateTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MentoringStatus mentoringStatus;

}
