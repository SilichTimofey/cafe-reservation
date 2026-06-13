package com.cafe.reservation.service;

import com.cafe.reservation.dto.TableRequestDTO;
import com.cafe.reservation.dto.TableResponseDTO;
import com.cafe.reservation.exception.DuplicateResourceException;
import com.cafe.reservation.exception.ResourceNotFoundException;
import com.cafe.reservation.mapper.CafeTableMapper;
import com.cafe.reservation.model.CafeTable;
import com.cafe.reservation.repository.CafeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeTableService {

    private final CafeTableRepository repository;
    private final CafeTableMapper mapper;

    public List<TableResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
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
