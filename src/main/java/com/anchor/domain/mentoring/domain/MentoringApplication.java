package com.anchor.domain.mentoring.domain;

import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringApplication extends BaseEntity {

  @Column(nullable = false)
  LocalDateTime startDateTime;

  @Column(nullable = false)
  LocalDateTime endDateTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MentoringStatus mentoringStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_id")
  private Mentoring mentoring;

  @OneToOne(mappedBy = "mentoringApplication")
  private Payment payment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_application_id")
  private User user;

  @Builder
  private MentoringApplication(LocalDateTime startDateTime, LocalDateTime endDateTime, MentoringStatus mentoringStatus,
      Mentoring mentoring, Payment payment, User user) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.mentoringStatus = mentoringStatus;
    this.mentoring = mentoring;
    this.payment = payment;
    this.user = user;
  }

  public void changeStatus(MentoringStatus mentoringStatus) {
    this.mentoringStatus = mentoringStatus;
  }
}
