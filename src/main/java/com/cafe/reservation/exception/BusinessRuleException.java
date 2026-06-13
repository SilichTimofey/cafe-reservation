package com.cafe.reservation.exception;

/**
 * Thrown when a domain invariant is violated (e.g. double-booking, invalid
 * time interval). Maps to HTTP 409 / 422 in the global handler.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
