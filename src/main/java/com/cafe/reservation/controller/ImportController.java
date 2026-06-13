package com.cafe.reservation.controller;

import com.cafe.reservation.dto.ImportResult;
import com.cafe.reservation.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    /**
     * Admin-only bulk import of cafe tables from an Excel (.xlsx/.xls) or CSV file.
     * Returns a per-row report rather than a binary success/failure.
     */
    @PostMapping("/import")
    public ImportResult importTables(@RequestParam("file") MultipartFile file) {
        return importService.importTables(file);
    }
}
