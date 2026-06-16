package com.cafe.reservation.repository;

import com.cafe.reservation.model.Reservation;
import com.cafe.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByTableId(Long tableId);

    List<Reservation> findByReservationDate(LocalDate reservationDate);

    List<Reservation> findByUserIdAndStatusIn(Long userId, Collection<ReservationStatus> statuses);

    List<Reservation> findByReservationDateAndReservationTimeAndStatusIn(
            LocalDate reservationDate, LocalTime reservationTime, Collection<ReservationStatus> statuses);

    boolean existsByTableIdAndReservationDateAndReservationTime(
            Long tableId, LocalDate reservationDate, LocalTime reservationTime);
}
