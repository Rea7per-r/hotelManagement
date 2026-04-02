package com.suraj.hotelManagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookingResponseDTO {

    private Long bookingId;

    private String customerName;
    private String roomNumber;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int guests;

    private String status;
}