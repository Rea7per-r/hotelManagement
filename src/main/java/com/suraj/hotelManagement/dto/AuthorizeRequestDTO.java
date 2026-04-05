package com.suraj.hotelManagement.dto;

import lombok.Data;

@Data
public class AuthorizeRequestDTO {
    private String username;
    private String codeChallenge;
}