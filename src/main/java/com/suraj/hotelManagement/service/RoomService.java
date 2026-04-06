package com.suraj.hotelManagement.service;

import com.suraj.hotelManagement.factory.RoomFactory;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);


    private final RoomRepository repo;

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    public Room createRoom(RoomType type, String roomNumber) {


        log.info("Creating room | roomNumber={} | type={}",
                roomNumber,type);
        Room room = RoomFactory.createRoom(type);
        room.setRoomNumber(roomNumber);

        log.info("Room created successfully | roomId={}", room.getRoomNumber());

        return repo.save(room);


    }

    public List<Room> getAll() {

        return repo.findAll();
    }

    public Room getById(Long id) {
        log.info("Request to get details of room id{}",id);
        return repo.findById(id).orElseThrow(()-> {
            log.error("can not find room with id{}",id);
            return new RuntimeException();
        });
    }

    public void delete(Long id) {

        log.info("Request to delete room of room id{}",id);
        repo.deleteById(id);
    }
}