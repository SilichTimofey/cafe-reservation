package com.cafe.reservation.service;

import com.cafe.reservation.dto.ReservationRequestDTO;
import com.cafe.reservation.dto.ReservationResponseDTO;
import com.cafe.reservation.exception.BusinessRuleException;
import com.cafe.reservation.exception.ResourceNotFoundException;
import com.cafe.reservation.mapper.ReservationMapper;
import com.cafe.reservation.model.CafeTable;
import com.cafe.reservation.model.Reservation;
import com.cafe.reservation.model.ReservationStatus;
import com.cafe.reservation.model.User;
import com.cafe.reservation.repository.CafeTableRepository;
import com.cafe.reservation.repository.ReservationRepository;
import com.cafe.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    /** Statuses considered active (still occupying a slot). */
    public static final List<ReservationStatus> ACTIVE_STATUSES =
            List.of(ReservationStatus.CONFIRMED);

    private final ReservationRepository repository;
    private final UserRepository userRepository;
    private final CafeTableRepository tableRepository;
    private final ReservationMapper mapper;

    public List<ReservationResponseDTO> findByUser(Long userId) {
        return repository.findByUserId(userId).stream().map(mapper::toResponse).toList();
    }

    public List<ReservationResponseDTO> findActiveByUser(Long userId) {
        return repository.findByUserIdAndStatusIn(userId, ACTIVE_STATUSES)
                .stream().map(mapper::toResponse).toList();
    }

    public ReservationResponseDTO findById(Long id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public ReservationResponseDTO create(ReservationRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        CafeTable table = tableRepository.findById(dto.tableId())
                .orElseThrow(() -> ResourceNotFoundException.of("CafeTable", dto.tableId()));

        if (dto.guestsCount() > table.getCapacity()) {
            throw new BusinessRuleException(
                    "Guests count %d exceeds table capacity %d".formatted(dto.guestsCount(), table.getCapacity()));
        }
        if (repository.existsByTableIdAndReservationDateAndReservationTime(
                table.getId(), dto.reservationDate(), dto.reservationTime())) {
            throw new BusinessRuleException("Table is already booked for this date and time");
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .table(table)
                .guestsCount(dto.guestsCount())
                .reservationDate(dto.reservationDate())
                .reservationTime(dto.reservationTime())
                .status(ReservationStatus.CONFIRMED)
                .build();
        return mapper.toResponse(repository.save(reservation));
    }

    @Transactional
    public ReservationResponseDTO cancel(Long id) {
        Reservation reservation = get(id);
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessRuleException("Reservation is already cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return mapper.toResponse(reservation);
    }

    private Reservation get(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Reservation", id));
    }
}
