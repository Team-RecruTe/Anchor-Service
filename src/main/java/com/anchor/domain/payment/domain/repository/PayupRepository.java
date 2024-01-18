package com.anchor.domain.payment.domain.repository;

import com.anchor.domain.payment.domain.Payup;
import com.anchor.domain.payment.domain.repository.custom.QPayupRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayupRepository extends JpaRepository<Payup, Long>, QPayupRepository {
}
