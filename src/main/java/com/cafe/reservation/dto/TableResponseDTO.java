package com.cafe.reservation.dto;

public record TableResponseDTO(
        Long id,
        String tableNumber,
        Integer capacity,
        boolean isVip
) {
}
