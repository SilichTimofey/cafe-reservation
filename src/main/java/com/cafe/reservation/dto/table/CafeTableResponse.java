package com.cafe.reservation.dto.table;

import com.cafe.reservation.model.TableStatus;

public record CafeTableResponse(
        Long id,
        String tableNumber,
        Integer capacity,
        String locationNote,
        TableStatus status
) {
}
