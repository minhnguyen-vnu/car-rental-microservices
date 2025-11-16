package com.paymentservice.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VnPayConfig {
    @Value("${vnpay.tmnCode}")     private String tmnCode;
    @Value("${vnpay.hashSecret}")  private String hashSecret;
    @Value("${vnpay.payUrl}")      private String payUrl;
    @Value("${vnpay.returnUrl}")   private String returnUrl;
    @Value("${vnpay.ipnUrl}")      private String ipnUrl;
}
