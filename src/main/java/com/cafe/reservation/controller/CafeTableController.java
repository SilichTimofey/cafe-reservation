package com.cafe.reservation.controller;

import com.cafe.reservation.dto.TableRequestDTO;
import com.cafe.reservation.dto.TableResponseDTO;
import com.cafe.reservation.service.CafeTableService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class CafeTableController {

    private final CafeTableService tableService;

    @GetMapping
    public List<TableResponseDTO> getAll() {
        return tableService.findAll();
    }

    @GetMapping("/available")
    public List<TableResponseDTO> getAvailable(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(schema = @Schema(type = "string", format = "date", example = "2026-06-15"))
            LocalDate date,
            @RequestParam
            @DateTimeFormat(pattern = "HH:mm")
            @Parameter(schema = @Schema(type = "string", example = "19:00"))
            LocalTime time,
            @RequestParam int guests) {
        return tableService.findAvailable(date, time, guests);
    }

    @GetMapping("/{id}")
    public TableResponseDTO getById(@PathVariable Long id) {
        return tableService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TableResponseDTO> create(@Valid @RequestBody TableRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(request));
    }

    @PutMapping("/{id}")
    public TableResponseDTO update(@PathVariable Long id, @Valid @RequestBody TableRequestDTO request) {
        return tableService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
