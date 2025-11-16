package com.rentalservice.core.constant.enums;

import lombok.Getter;

@Getter
public enum PaymentResult {
    SUCCEEDED("succeeded"),
    FAILED("failed");

    private final String result;

    PaymentResult(String result) {
        this.result = result;
    }
}
