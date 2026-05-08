package com.example.demo.scheduler;

import com.example.demo.service.BlacklistCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlacklistResetScheduler {

    private final BlacklistCalculationService blacklistCalculationService;

    /**
     * Recalculates blacklist points for all students daily.
     * Runs at 00:00:00 every day.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void recalculateBlacklistPoints() {
        blacklistCalculationService.recalculateAllStudentsPoints();
    }
}
