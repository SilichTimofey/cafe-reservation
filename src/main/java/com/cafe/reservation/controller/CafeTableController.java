package com.cafe.reservation.controller;

import com.cafe.reservation.dto.table.CafeTableRequest;
import com.cafe.reservation.dto.table.CafeTableResponse;
import com.cafe.reservation.service.CafeTableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
public class CafeTableController {

    private final CafeTableService tableService;

    public CafeTableController(CafeTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public List<CafeTableResponse> getAll() {
        return tableService.findAll();
    }

    @GetMapping("/{id}")
    public CafeTableResponse getById(@PathVariable Long id) {
        return tableService.findById(id);
    }

    @PostMapping
    public ResponseEntity<CafeTableResponse> create(@Valid @RequestBody CafeTableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(request));
    }

    @PutMapping("/{id}")
    public CafeTableResponse update(@PathVariable Long id, @Valid @RequestBody CafeTableRequest request) {
        return tableService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
