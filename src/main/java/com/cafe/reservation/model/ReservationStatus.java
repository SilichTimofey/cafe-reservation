package com.cafe.reservation.model;

/**
 * Lifecycle of a reservation. Kept as an enum to avoid "magic strings"
 * and to allow state-transition validation in the service layer.
 */
public enum ReservationStatus {
    CONFIRMED,
    CANCELLED
}
