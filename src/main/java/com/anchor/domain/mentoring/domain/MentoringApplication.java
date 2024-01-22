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
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MentoringApplication extends BaseEntity {

  @Column(nullable = false, columnDefinition = "datetime")
  private LocalDateTime startDateTime;

  @Column(nullable = false, columnDefinition = "datetime")
  private LocalDateTime endDateTime;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
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

  @OneToOne(mappedBy = "mentoringApplication")
  private MentoringReview mentoringReview;

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