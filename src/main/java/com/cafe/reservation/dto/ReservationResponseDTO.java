package com.cafe.reservation.dto;

import com.cafe.reservation.model.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationResponseDTO(
        Long id,
        String userName,
        String tableNumber,
        Integer guestsCount,
        LocalDate reservationDate,
        LocalTime reservationTime,
        ReservationStatus status
) {
}
