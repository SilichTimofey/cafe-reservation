package com.cafe.reservation.repository;

import com.cafe.reservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByTableId(Long tableId);

    List<Reservation> findByReservationDate(LocalDate reservationDate);

    boolean existsByTableIdAndReservationDateAndReservationTime(
            Long tableId, LocalDate reservationDate, java.time.LocalTime reservationTime);
}
