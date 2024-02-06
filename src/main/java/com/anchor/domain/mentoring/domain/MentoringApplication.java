package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.domain.User;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="mentoring_application")
public class MentoringApplication extends BaseEntity {

  @Column(name="start_date_time", nullable = false, columnDefinition = "datetime")
  private LocalDateTime startDateTime;

  @Column(name="end_date_time", nullable = false, columnDefinition = "datetime")
  private LocalDateTime endDateTime;

  @Column(name="has_review", nullable = false, columnDefinition = "boolean default false")
  private Boolean hasReview = Boolean.FALSE;

  @Enumerated(EnumType.STRING)
  @Column(name="mentoring_status", nullable = false)
  private MentoringStatus mentoringStatus = MentoringStatus.WAITING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_id")
  private Mentoring mentoring;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  private MentoringApplication(LocalDateTime startDateTime, LocalDateTime endDateTime, Mentoring mentoring,
      Payment payment, User user) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.mentoring = mentoring;
    this.payment = payment;
    this.user = user;
    this.user.getMentoringApplicationList()
        .add(this);
  }

  public MentoringApplication(MentoringApplicationInfo applicationInfo, Mentoring mentoring, Payment payment,
      User user) {
    this.startDateTime = applicationInfo.getStartDateTime();
    this.endDateTime = applicationInfo.getEndDateTime();
    this.mentoring = mentoring;
    this.payment = payment;
    this.payment.addMentoringApplication(this);
    this.user = user;
    this.user.getMentoringApplicationList()
        .add(this);
  }

  public void connectPayment(Payment payment) {
    this.payment = payment;
  }

  public void changeStatus(MentoringStatus mentoringStatus) {
    this.mentoringStatus = mentoringStatus;
  }

  public boolean isNotCancelled() {
    return mentoringStatus != MentoringStatus.CANCELLED;
  }

  public void completedReview() {
    hasReview = true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof MentoringApplication that)) {
      return false;
    }
    return Objects.equals(getStartDateTime(), that.getStartDateTime())
        && Objects.equals(getEndDateTime(), that.getEndDateTime())
        && getMentoringStatus() == that.getMentoringStatus() && Objects.equals(
        getMentoring(), that.getMentoring()) && Objects.equals(getPayment(),
        that.getPayment()) && Objects.equals(getUser(), that.getUser());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStartDateTime(), getEndDateTime(), getMentoringStatus(), getMentoring(),
        getPayment(), getUser());
  }

}