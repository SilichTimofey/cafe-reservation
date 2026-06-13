package com.cafe.reservation.dto;

import java.time.LocalDateTime;

public record ReviewResponseDTO(
        Long id,
        String userName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
