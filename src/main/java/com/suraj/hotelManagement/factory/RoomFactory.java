package com.suraj.hotelManagement.factory;

import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomStatus;
import com.suraj.hotelManagement.model.enums.RoomType;

public class RoomFactory {

    public static Room createRoom(RoomType type) {

        Room room = new Room();

        room.setRoomType(type);
        room.setStatus(RoomStatus.AVAILABLE);

        switch (type) {
            case STANDARD:
                room.setPricePerNight(1000);
                room.setCapacity(2);
                room.setAmenities("Basic");
                break;

            case DELUXE:
                room.setPricePerNight(2500);
                room.setCapacity(3);
                room.setAmenities("WiFi, AC");
                break;

            case SUITE:
                room.setPricePerNight(5000);
                room.setCapacity(5);
                room.setAmenities("WiFi, AC, TV, Mini Bar");
                break;
        }

        return room;
    }
}