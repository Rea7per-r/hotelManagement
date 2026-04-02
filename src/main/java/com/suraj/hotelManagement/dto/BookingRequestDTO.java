package com.suraj.hotelManagement.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequestDTO {

    private Long customerId;
    private Long roomId;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private int guests;
}