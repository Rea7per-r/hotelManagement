package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.factory.RoomFactory;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private final RoomRepository repo;

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    public Room createRoom(RoomType type, String roomNumber) {
        Room room = RoomFactory.createRoom(type);
        room.setRoomNumber(roomNumber);
        return repo.save(room);
    }

    public List<Room> getAll() {
        return repo.findAll();
    }

    public Room getById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}