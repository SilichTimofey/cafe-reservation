package com.cafe.reservation.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Report of a bulk import (Excel/CSV). Reports partial success rather than a
 * binary success/failure so the caller can see which rows were rejected and why.
 */
public class ImportResult {

    private int totalRows;
    private int imported;
    private final List<RowError> errors = new ArrayList<>();

    public void incrementTotal() {
        totalRows++;
    }

    public void incrementImported() {
        imported++;
    }

    public void addError(int rowNumber, String reason) {
        errors.add(new RowError(rowNumber, reason));
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getImported() {
        return imported;
    }

    public int getRejected() {
        return errors.size();
    }

    public List<RowError> getErrors() {
        return errors;
    }

    public record RowError(int rowNumber, String reason) {
    }
}
