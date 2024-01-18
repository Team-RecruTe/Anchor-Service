package com.anchor.domain.payment.domain;

import com.anchor.domain.mentor.domain.Mentor;
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
public class Payup extends BaseEntity {

  private Integer amount;

  @Column(columnDefinition = "datetime")
  private LocalDateTime payupDateTime;

  @Enumerated(EnumType.STRING)
  private PayupStatus payupStatus = PayupStatus.WAITING;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentor_id")
  private Mentor mentor;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "payment_id")
  private Payment payment;

  @Builder
  private Payup(LocalDateTime payupDateTime, Mentor mentor, Payment payment) {
    this.amount = payment.getAmount();
    this.payupDateTime = payupDateTime;
    this.mentor = mentor;
    this.mentor.getPayups()
        .add(this);
    this.payment = payment;
  }

  public void changeStatusToComplete() {
    this.payupStatus = PayupStatus.COMPLETE;
  }

}
