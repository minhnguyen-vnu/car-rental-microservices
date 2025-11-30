package com.rentalservice.core.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDateTime;

@DynamicInsert
@Entity
@Table(name = "rentals")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "transaction_code")
    private String transactionCode;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "payment_id")
    private Integer paymentId;

    @Column(name = "pickup_time")
    private LocalDateTime pickupTime;

    @Column(name = "return_time")
    private LocalDateTime returnTime;

    @Column(name = "pickup_branch_id")
    private Integer pickupBranchId;

    @Column(name = "return_branch_id")
    private Integer returnBranchId;

    @Column(name = "duration_days")
    private Double durationDays;

    @Column(name = "total_amount")
    private Double totalAmount;

    private String currency;
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
