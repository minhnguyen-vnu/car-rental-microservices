package com.rentalservice.core.job;

import com.rentalservice.core.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbSweepJob {
    private final RentalService rentalService;

    @Scheduled(cron = "0 */10 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void run() {
        rentalService.sync();
    }
}
