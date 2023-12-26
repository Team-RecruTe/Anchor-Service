package com.anchor.domain.payment.domain;

import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment extends BaseEntity {

  @Column(nullable = false, unique = true)
  private String impUid;

  @Column(nullable = false, unique = true)
  private String merchantUid;

  @Column(nullable = false)
  private Integer amount;

  private Integer cancelAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus paymentStatus;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mentoring_application_id")
  private MentoringApplication mentoringApplication;

}
