package com.cafe.reservation.dto;

import com.cafe.reservation.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank String name,
        @NotBlank String phoneNumber,
        @NotNull Role role
) {
}
