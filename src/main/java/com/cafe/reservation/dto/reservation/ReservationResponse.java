package com.cafe.reservation.dto.reservation;

import com.cafe.reservation.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long tableId,
        String tableNumber,
        Long userId,
        String userEmail,
        Integer guestCount,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status,
        String notes
) {
}
