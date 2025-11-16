package com.paymentservice.core.enums;

import lombok.Getter;

@Getter
public enum VnPayPaymentStatus {
    SUCCESS("00");

    private final String code;

    VnPayPaymentStatus(String code) {
        this.code = code;
    }
}
