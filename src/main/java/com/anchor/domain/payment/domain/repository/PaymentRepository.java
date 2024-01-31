package com.anchor.domain.payment.domain.repository;

import com.anchor.domain.payment.domain.Payment;
import com.anchor.domain.payment.domain.repository.custom.QPaymentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, QPaymentRepository {

}
