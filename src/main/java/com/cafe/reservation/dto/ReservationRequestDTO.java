package com.cafe.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequestDTO(
        @NotNull Long tableId,
        @NotNull @Min(1) Integer guestsCount,
        @NotNull @Future
        @Schema(type = "string", format = "date", example = "2026-07-01")
        LocalDate reservationDate,
        @NotNull
        @Schema(type = "string", example = "19:00")
        LocalTime reservationTime
) {
}
