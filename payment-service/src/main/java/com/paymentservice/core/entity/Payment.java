package com.paymentservice.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@DynamicInsert
@Entity
@Table(name = "payments")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "payment_code")
    private String paymentCode;

    @Column(name = "rental_id")
    private Integer rentalId;

    @Column(name = "user_id")
    private Integer userId;

    private BigDecimal amount;

    private String currency;

    private String method;

    private String provider;

    @Column(name = "provider_txn_id")
    private String providerTxnId;

    private String status;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;


}
