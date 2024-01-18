package com.anchor.domain.payment.domain;

import com.anchor.domain.mentoring.api.controller.request.MentoringApplicationInfo;
import com.anchor.domain.mentoring.domain.MentoringApplication;
import com.anchor.global.portone.response.PaymentCancelResult;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
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

  @Column(nullable = false, unique = true)
  private String orderUid;

  @Column(nullable = false)
  private Integer amount;

  @Column(nullable = false)
  private Integer cancelAmount = 0;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PaymentStatus paymentStatus = PaymentStatus.SUCCESS;

  @OneToOne(mappedBy = "payment", fetch = FetchType.LAZY)
  private MentoringApplication mentoringApplication;

  @Builder
  private Payment(String impUid, String merchantUid, Integer amount, MentoringApplication mentoringApplication) {
    this.impUid = impUid;
    this.merchantUid = merchantUid;
    this.orderUid = "anchor_" + merchantUid.substring(merchantUid.indexOf('_'));
    this.amount = amount;
    this.mentoringApplication = mentoringApplication;
  }

  public Payment(MentoringApplicationInfo applicationInfo) {
    this.impUid = applicationInfo.getImpUid();
    this.merchantUid = applicationInfo.getMerchantUid();
    this.orderUid = "anchor" + applicationInfo.getMerchantUid()
        .substring(merchantUid.indexOf('_'));
    this.amount = applicationInfo.getAmount();
    this.cancelAmount = 0;
    this.paymentStatus = PaymentStatus.SUCCESS;
  }

  public boolean isCancelled() {
    return this.paymentStatus.equals(PaymentStatus.CANCELLED);
  }

  public void editPaymentCancelStatus(PaymentCancelResult PaymentCancelDetail) {
    this.paymentStatus = PaymentStatus.CANCELLED;
    this.cancelAmount = PaymentCancelDetail.getCancelAmount();
  }

  public void addMentoringApplication(MentoringApplication mentoringApplication) {
    this.mentoringApplication = mentoringApplication;
  }
}
