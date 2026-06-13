package com.cafe.reservation.service;

import com.cafe.reservation.dto.ImportResult;
import com.cafe.reservation.exception.BusinessRuleException;
import com.cafe.reservation.model.CafeTable;
import com.cafe.reservation.repository.CafeTableRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Bulk-imports {@link CafeTable} rows from Excel (.xlsx/.xls via Apache POI)
 * or CSV (Apache Commons CSV). Expected columns: tableNumber, capacity, isVip.
 *
 * <p>The import reports partial success: a malformed row is skipped and recorded
 * in {@link ImportResult}, while valid rows are still persisted.
 */
@Service
@RequiredArgsConstructor
public class ImportService {

    private final CafeTableRepository tableRepository;
    private final DataFormatter dataFormatter = new DataFormatter();

    @Transactional
    public ImportResult importTables(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("Uploaded file is empty");
        }
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BusinessRuleException("Uploaded file has no name");
        }
        String lower = filename.toLowerCase();
        try (InputStream in = file.getInputStream()) {
            if (lower.endsWith(".csv")) {
                return importFromCsv(in);
            } else if (lower.endsWith(".xlsx") || lower.endsWith(".xls")) {
                return importFromExcel(in);
            }
            throw new BusinessRuleException("Unsupported file type. Use .csv, .xls or .xlsx");
        } catch (IOException ex) {
            throw new BusinessRuleException("Failed to read file: " + ex.getMessage());
        }
    }

    private ImportResult importFromExcel(InputStream in) throws IOException {
        ImportResult result = new ImportResult();
        try (Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean headerSkipped = false;
            for (Row row : sheet) {
                if (!headerSkipped) {
                    headerSkipped = true; // first row is the header
                    continue;
                }
                result.incrementTotal();
                int rowNum = row.getRowNum() + 1;
                try {
                    String tableNumber = stringValue(row.getCell(0));
                    Integer capacity = intValue(row.getCell(1));
                    boolean vip = boolValue(stringValue(row.getCell(2)));
                    persist(tableNumber, capacity, vip, rowNum, result);
                } catch (Exception ex) {
                    result.addError(rowNum, ex.getMessage());
                }
            }
        }
        return result;
    }

    private ImportResult importFromCsv(InputStream in) throws IOException {
        ImportResult result = new ImportResult();
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setTrim(true)
                .build();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
             CSVParser parser = format.parse(reader)) {
            for (CSVRecord record : parser) {
                result.incrementTotal();
                int rowNum = (int) record.getRecordNumber() + 1;
                try {
                    String tableNumber = record.get("tableNumber");
                    Integer capacity = Integer.parseInt(record.get("capacity").trim());
                    boolean vip = record.isMapped("isVip") && boolValue(record.get("isVip"));
                    persist(tableNumber, capacity, vip, rowNum, result);
                } catch (Exception ex) {
                    result.addError(rowNum, ex.getMessage());
                }
            }
        }
        return result;
    }

    private void persist(String tableNumber, Integer capacity, boolean vip, int rowNum, ImportResult result) {
        if (tableNumber == null || tableNumber.isBlank()) {
            result.addError(rowNum, "tableNumber is required");
            return;
        }
        if (capacity == null || capacity < 1) {
            result.addError(rowNum, "capacity must be a positive integer");
            return;
        }
        if (tableRepository.existsByTableNumber(tableNumber)) {
            result.addError(rowNum, "Duplicate tableNumber: " + tableNumber);
            return;
        }
        tableRepository.save(CafeTable.builder()
                .tableNumber(tableNumber)
                .capacity(capacity)
                .isVip(vip)
                .build());
        result.incrementImported();
    }

    private String stringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String value = dataFormatter.formatCellValue(cell).trim();
        return value.isEmpty() ? null : value;
    }

    private Integer intValue(Cell cell) {
        String value = stringValue(cell);
        return value == null ? null : Integer.parseInt(value);
    }

    private boolean boolValue(String raw) {
        if (raw == null) {
            return false;
        }
        String v = raw.trim().toLowerCase();
        return v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("vip");
    }
}
