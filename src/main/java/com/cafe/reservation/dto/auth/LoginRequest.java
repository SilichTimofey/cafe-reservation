package com.cafe.reservation.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "Номер телефона не может быть пустым")
        @Pattern(regexp = "^\\+375(25|29|33|44)\\d{7}$",
                message = "Номер должен быть в формате +375XXXXXXXXX (допустимые коды операторов: 25, 29, 33, 44)")
        String phoneNumber,
        @NotBlank String name
) {
}
