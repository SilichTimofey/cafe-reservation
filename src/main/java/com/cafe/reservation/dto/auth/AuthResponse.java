package com.cafe.reservation.dto.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String role
) {
}
