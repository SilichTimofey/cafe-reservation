package com.cafe.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequestDTO(
        @NotNull Long tableId,
        @NotNull @Min(1) Integer guestsCount,
        @NotNull @Future LocalDate reservationDate,
        @NotNull LocalTime reservationTime
) {
}
