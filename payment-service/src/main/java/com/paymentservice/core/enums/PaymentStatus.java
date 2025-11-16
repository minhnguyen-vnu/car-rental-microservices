package com.paymentservice.core.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    STATUS_PENDING("PENDING"),
    STATUS_FAILED("FAILED"),
    STATUS_SUCCEEDED("SUCCEEDED");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }
}
