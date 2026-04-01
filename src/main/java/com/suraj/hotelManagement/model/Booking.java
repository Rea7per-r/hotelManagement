package com.suraj.hotelManagement.model;

import com.suraj.hotelManagement.model.enums.BookingStatus;
import com.suraj.hotelManagement.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    // Rls with Customer
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Rls with Room
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private int numberOfGuests;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

//    @Enumerated(EnumType.STRING)
//    private PaymentStatus paymentStatus;

    //private Double totalAmount;
}