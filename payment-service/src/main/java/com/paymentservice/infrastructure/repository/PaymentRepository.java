package com.paymentservice.infrastructure.repository;

import com.paymentservice.core.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentCode(String paymentCode);
    boolean existsByPaymentCode(String paymentCode);
}
