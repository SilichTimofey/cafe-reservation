package com.cafe.reservation.service;

import com.cafe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Periodically auto-marks no-show reservations: a still-active booking whose
 * slot (reservationDate + reservationTime) is more than {@code GRACE_MINUTES}
 * in the past is switched to NO_SHOW.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationScheduler {

    /** How long a guest is awaited after the slot start before it counts as a no-show. */
    private static final int GRACE_MINUTES = 15;

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void cancelExpiredReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(GRACE_MINUTES);
        int updated = reservationRepository.markExpiredAsNoShow(threshold);
        if (updated > 0) {
            log.info("Auto NO_SHOW: marked {} expired reservation(s)", updated);
        }
    }
}
