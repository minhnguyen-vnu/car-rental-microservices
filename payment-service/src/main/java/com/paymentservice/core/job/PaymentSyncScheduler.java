package com.paymentservice.core.job;

import com.paymentservice.core.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentSyncScheduler {
    private final PaymentService paymentService;
    @Scheduled(cron = "*/30 * * * * *", zone = "Asia/Ho_Chi_Minh")
    public void run() {
        log.info("=== SCHEDULER RUNNING: {} ===", LocalDateTime.now());
        paymentService.sync();
    }
}
