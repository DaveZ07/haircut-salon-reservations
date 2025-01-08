package com.salonfryzjerski.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String message;
    private String role;
    private Long userId;
    private String username;
}