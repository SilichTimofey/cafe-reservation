package com.cafe.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TableRequestDTO(
        @NotBlank String tableNumber,
        @NotNull @Min(1) Integer capacity,
        boolean isVip
) {
}
