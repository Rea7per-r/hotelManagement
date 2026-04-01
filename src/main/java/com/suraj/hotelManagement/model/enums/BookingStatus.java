package com.suraj.hotelManagement.model.enums;

import jakarta.persistence.Enumerated;

public enum BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED
}
