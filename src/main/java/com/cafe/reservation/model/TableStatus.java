package com.cafe.reservation.model;

/**
 * Operational status of a physical table. OUT_OF_SERVICE tables are excluded
 * from availability searches regardless of existing reservations.
 */
public enum TableStatus {
    AVAILABLE,
    OUT_OF_SERVICE
}
