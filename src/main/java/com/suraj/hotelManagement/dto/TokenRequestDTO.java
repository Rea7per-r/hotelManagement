package com.suraj.hotelManagement.dto;

import lombok.Data;

@Data
public class TokenRequestDTO {
    private String authCode;
    private String codeVerifier;
}