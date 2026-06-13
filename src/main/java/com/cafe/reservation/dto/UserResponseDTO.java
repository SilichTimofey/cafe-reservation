package com.cafe.reservation.dto;

import com.cafe.reservation.model.Role;

public record UserResponseDTO(
        Long id,
        String name,
        String phoneNumber,
        Role role
) {
}
