package com.suraj.hotelManagement.dto;

import com.suraj.hotelManagement.model.enums.RoomType;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
@Data
public class RoomRequestDTO {

    @NotNull()
    private RoomType type;

    @NotNull()
    private String roomNumber;

}