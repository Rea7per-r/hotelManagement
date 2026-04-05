package com.suraj.hotelManagement.controller;

import com.suraj.hotelManagement.dto.RoomRequestDTO;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.service.RoomService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService service;

    public RoomController(RoomService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createRoom(@RequestBody RoomRequestDTO request) {
        service.createRoom(request.getType(), request.getRoomNumber());
        return "Successfully Created Room";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/get")
    public List<Room> getAll() {
        return service.getAll();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("get/{id}")
    public Room getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("remove/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}