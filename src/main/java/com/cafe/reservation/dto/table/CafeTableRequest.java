package com.cafe.reservation.dto.table;

import com.cafe.reservation.model.TableStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CafeTableRequest(
        @NotBlank String tableNumber,
        @NotNull @Min(1) @Max(50) Integer capacity,
        String locationNote,
        @NotNull TableStatus status
) {
}
