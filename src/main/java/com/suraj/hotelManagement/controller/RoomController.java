package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.RoomRequestDTO;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public String createRoom(@RequestBody RoomRequestDTO request) {
        service.createRoom(request.getType(), request.getRoomNumber());
        return "Successfully Created Room";
    }

    @GetMapping("/get")
    public List<Room> getAll() {
        return service.getAll();
    }

    @GetMapping("get/{id}")
    public Room getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @DeleteMapping("remove/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}