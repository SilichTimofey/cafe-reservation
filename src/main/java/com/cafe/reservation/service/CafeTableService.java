package com.cafe.reservation.service;

import com.cafe.reservation.dto.TableRequestDTO;
import com.cafe.reservation.dto.TableResponseDTO;
import com.cafe.reservation.exception.DuplicateResourceException;
import com.cafe.reservation.exception.ResourceNotFoundException;
import com.cafe.reservation.mapper.CafeTableMapper;
import com.cafe.reservation.model.CafeTable;
import com.cafe.reservation.repository.CafeTableRepository;
import com.cafe.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeTableService {

    private final CafeTableRepository repository;
    private final ReservationRepository reservationRepository;
    private final CafeTableMapper mapper;

    public List<TableResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    /** Tables fitting the party size and not booked at the given date/time. */
    public List<TableResponseDTO> findAvailable(LocalDate date, LocalTime time, int guests) {
        Set<Long> booked = reservationRepository
                .findByReservationDateAndReservationTimeAndStatusIn(date, time, ReservationService.ACTIVE_STATUSES)
                .stream().map(r -> r.getTable().getId()).collect(Collectors.toSet());
        return repository.findAll().stream()
                .filter(t -> t.getCapacity() >= guests && !booked.contains(t.getId()))
                .map(mapper::toResponse).toList();
    }

    public TableResponseDTO findById(Long id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public TableResponseDTO create(TableRequestDTO dto) {
        if (repository.existsByTableNumber(dto.tableNumber())) {
            throw new DuplicateResourceException("Table number already exists: " + dto.tableNumber());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public TableResponseDTO update(Long id, TableRequestDTO dto) {
        CafeTable table = get(id);
        mapper.update(table, dto);
        return mapper.toResponse(table);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }

    private CafeTable get(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("CafeTable", id));
    }
}
