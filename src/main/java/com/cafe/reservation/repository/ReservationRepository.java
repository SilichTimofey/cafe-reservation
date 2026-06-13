package com.cafe.reservation.repository;

import com.cafe.reservation.model.Reservation;
import com.cafe.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * Single set-based UPDATE that flips every still-active (PENDING/CONFIRMED) reservation
     * whose slot start is already past the grace threshold to NO_SHOW. Done in one SQL
     * statement (no entity loading, no N+1) so the scheduled job stays cheap; PostgreSQL
     * combines (reservation_date + reservation_time) into a timestamp for the comparison.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE reservations
            SET status = 'NO_SHOW'
            WHERE status IN ('PENDING', 'CONFIRMED')
              AND (reservation_date + reservation_time) < :threshold
            """, nativeQuery = true)
    int markExpiredAsNoShow(@Param("threshold") LocalDateTime threshold);
}
