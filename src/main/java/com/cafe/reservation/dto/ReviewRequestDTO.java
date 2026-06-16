package com.cafe.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReviewRequestDTO(
        @NotNull @Min(1) @Max(5) @Schema(example = "5") Integer rating,
        @Schema(example = "Отличное место, уютная атмосфера") String comment
) {
}
