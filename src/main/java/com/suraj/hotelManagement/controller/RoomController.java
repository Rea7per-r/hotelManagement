package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.RoomRequestDTO;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService service;

    private static final Logger log = LoggerFactory.getLogger(RoomController.class);

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createRoom(@RequestBody RoomRequestDTO request) {

        log.info("Request received to create room | roomNumber={} | type={}",
                request.getRoomNumber(), request.getType());

        service.createRoom(request.getType(), request.getRoomNumber());


        return "Successfully Created Room";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/get")
    public List<Room> getAll() {

        log.info("Request received to fetch all rooms");

        List<Room> rooms = service.getAll();


        return rooms;
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/get/{id}")
    public Room getById(@PathVariable Long id) {

        log.info("Request received to fetch room | id={}", id);

        Room room = service.getById(id);

        log.info("Room fetched successfully | id={} | roomNumber={}",
                id, room.getRoomNumber());

        return room;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove/{id}")
    public void delete(@PathVariable Long id) {

        log.info("Request received to delete room | id={}", id);

        service.delete(id);

        log.info("Room deleted successfully | id={}", id);
    }
}