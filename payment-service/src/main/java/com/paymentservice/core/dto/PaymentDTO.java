package com.paymentservice.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentDTO {
    private Integer id;
    private String paymentCode;
    private Integer rentalId;
    private Integer userId;
    private BigDecimal amount;
    private String currency;
    private String method;
    private String provider;
    private String providerTxnId;
    private String status;
    private BigDecimal refundAmount;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
}
