package com.suraj.hotelManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder

@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {

    private Long bookingId;

    private String customerName;
    private String roomNumber;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private int guests;

    private String status;
}