package com.suraj.hotelManagement.dto;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String username;
    private String password;
}