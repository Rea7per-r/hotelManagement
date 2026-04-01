package com.suraj.hotelManagement.model;

import com.suraj.hotelManagement.model.enums.RoomStatus;
import com.suraj.hotelManagement.model.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(unique = true, nullable = false)
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private double pricePerNight;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    private int capacity;

    private String amenities;
}