package com.cafe.reservation.mapper;

import com.cafe.reservation.dto.ReservationResponseDTO;
import com.cafe.reservation.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDTO toResponse(Reservation r) {
        return new ReservationResponseDTO(
                r.getId(),
                r.getUser().getName(),
                r.getTable().getTableNumber(),
                r.getGuestsCount(),
                r.getReservationDate(),
                r.getReservationTime(),
                r.getStatus());
    }
}
