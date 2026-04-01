package com.suraj.hotelManagement.repository;

import com.suraj.hotelManagement.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends  JpaRepository<Room, Long> {
}
