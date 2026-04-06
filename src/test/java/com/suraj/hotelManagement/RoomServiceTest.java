package com.suraj.hotelManagement;

import com.suraj.hotelManagement.dto.RoomRequestDTO;
import com.suraj.hotelManagement.model.Payment;
import com.suraj.hotelManagement.model.Room;
import com.suraj.hotelManagement.model.enums.RoomStatus;
import com.suraj.hotelManagement.model.enums.RoomType;
import com.suraj.hotelManagement.repository.RoomRepository;
import com.suraj.hotelManagement.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    RoomRepository roomRepository;

    @InjectMocks
    RoomService roomService;

    @Test
    void shouldCreateRoomSucessfully() {
        RoomRequestDTO dto = new RoomRequestDTO();
        dto.setRoomNumber("101");
        dto.setType(RoomType.SUITE);

        Room room = new Room();
        room.setRoomNumber("101");
        room.setCapacity(5);
        room.setRoomType(RoomType.SUITE);

        when(roomRepository.save(any())).thenReturn(room);
        Room result = roomService.createRoom(dto.getType(), dto.getRoomNumber());

        assertNotNull(result);
        assertEquals("101", result.getRoomNumber());

        verify(roomRepository).save(any());
    }


        @Test
        void shouldReturnAllRooms() {

        when(roomRepository.findAll()).thenReturn(Collections.singletonList(new Room()));

        assertEquals(1, roomService.getAll().size());
    }

    @Test
    void shouldReturnByRoomId()
    {
        Room room=new Room();
        room.setRoomId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result= roomService.getById(1L);
        assertNotNull(result);
        assertEquals(1L,result.getRoomId());
    }





    }

