package com.suraj.hotelManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.suraj.hotelManagement.dto.RoomRequestDTO;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest {
    @MockBean
    private com.suraj.hotelManagement.security.JwtFilter jwtFilter;

    @MockBean
    private com.suraj.hotelManagement.security.CustomUserDetailsService customUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    //  CREATE ROOM (no role required now)
    @Test
    void shouldCreateRoom() throws Exception {

        RoomRequestDTO request = new RoomRequestDTO();
        request.setRoomNumber("101");
        request.setType(RoomType.DELUXE);

        mockMvc.perform(post("/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Created Room"));

        verify(roomService).createRoom(RoomType.DELUXE, "101");
    }

    //  GET ALL
    @Test
    void shouldGetAllRooms() throws Exception {

        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");

        when(roomService.getAll()).thenReturn(List.of(room));

        mockMvc.perform(get("/rooms/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    //  GET BY ID
    @Test
    void shouldGetRoomById() throws Exception {

        Room room = new Room();
        room.setRoomId(1L);
        room.setRoomNumber("101");

        when(roomService.getById(1L)).thenReturn(room);

        mockMvc.perform(get("/rooms/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("101"));
    }

    // DELETE (no role required now)
    @Test
    void shouldDeleteRoom() throws Exception {

        mockMvc.perform(delete("/rooms/remove/1"))
                .andExpect(status().isOk());

        verify(roomService).delete(1L);
    }
}