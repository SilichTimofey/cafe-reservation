package com.cafe.reservation.model;

/**
 * Application roles. Spring Security expects authorities prefixed with "ROLE_",
 * which is handled where authorities are built from this enum.
 */
public enum Role {
    USER,
    ADMIN
}
