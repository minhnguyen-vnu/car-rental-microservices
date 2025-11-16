package com.paymentservice.kernel.mapper;

import com.paymentservice.core.dto.PaymentDTO;
import com.paymentservice.core.dto.response.PaymentChargeResponseDTO;
import com.paymentservice.core.entity.Payment;

public class PaymentMapper {

    private PaymentMapper() {}

    public static PaymentDTO toDTO(Payment p) {
        if (p == null) return null;
        return PaymentDTO.builder()
                .id(p.getId())
                .paymentCode(p.getPaymentCode())
                .rentalId(p.getRentalId())
                .userId(p.getUserId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .method(p.getMethod())
                .provider(p.getProvider())
                .providerTxnId(p.getProviderTxnId())
                .status(p.getStatus())
                .refundAmount(p.getRefundAmount())
                .paidAt(p.getPaidAt())
                .refundedAt(p.getRefundedAt())
                .build();
    }

    public static PaymentChargeResponseDTO toChargeResponse(Payment p, String checkoutUrl) {
        if (p == null) return null;
        return PaymentChargeResponseDTO.builder()
                .paymentCode(p.getPaymentCode())
                .status(p.getStatus())
                .checkoutUrl(checkoutUrl)
                .providerTxnId(p.getProviderTxnId())
                .paidAt(p.getPaidAt())
                .refundAmount(p.getRefundAmount())
                .build();
    }
}
