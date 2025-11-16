package com.paymentservice.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Status {
    private int code;
    private String displayMessage;
}
