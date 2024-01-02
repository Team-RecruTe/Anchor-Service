package com.anchor.domain.mentoring.domain;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationTime;
import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.user.api.controller.request.AppliedMentoringStatus;
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
  private MentoringStatus mentoringStatus;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_id")
  private Mentoring mentoring;

  @OneToOne(mappedBy = "mentoringApplication")
  private Payment payment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
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


  public MentoringApplication(MentoringApplicationTime mentoringApplicationTime,
      MentoringStatus mentoringStatus, Mentoring mentoring, Payment payment, User user) {
    this.startDateTime = mentoringApplicationTime.getFromDateTime();
    this.endDateTime = mentoringApplicationTime.getToDateTime();
    this.mentoringStatus = mentoringStatus == null ? MentoringStatus.WAITING : mentoringStatus;
    this.mentoring = mentoring;
    this.payment = payment;
    this.user = user;
  }

  public void changeStatus(MentoringStatus mentoringStatus) {
    this.mentoringStatus = mentoringStatus;
  }

  public boolean isChangedMentoringStatus(MentoringStatus changeStatus) {

    return this.mentoringStatus.equals(changeStatus);
  }

  public boolean isMatchingDateTime(AppliedMentoringStatus appliedMentoringStatus) {

    return this.startDateTime.isEqual(appliedMentoringStatus.getStartDateTime())
        &&
        this.endDateTime.isEqual(appliedMentoringStatus.getEndDateTime());
  }

  public void changePayment(Payment payment) {
    setPayment(payment);
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
