package com.anchor.domain.payment.domain;

import com.anchor.domain.mentor.domain.Mentor;
import com.anchor.global.util.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payup extends BaseEntity {

    // 정산 관련 기능 추가 필요

    private Integer amount;

    @Column(columnDefinition = "datetime")
    private LocalDateTime payUpDateTime;

    @Enumerated(EnumType.STRING)
    private PayupStatus payupStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id")
    private Mentor mentor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
