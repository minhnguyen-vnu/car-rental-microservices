package com.paymentservice.infrastructure.repository;

import com.paymentservice.core.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByPaymentCode(String paymentCode);
    boolean existsByPaymentCode(String paymentCode);

    @Query("""
        select p from Payment p
            where p.createdAt < :now
                and p.status = 'PENDING'
    """)
    List<Payment> findDuePayments(@Param("now") LocalDateTime now);
}
