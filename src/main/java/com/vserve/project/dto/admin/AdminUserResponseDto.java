package com.vserve.project.dto.admin;

import java.time.LocalDateTime;

public record AdminUserResponseDto(

        Long id,
        String username,
        String email,
        String role,
        String status,
        com.vserve.project.enums.Availability availability,
        LocalDateTime createdAt

) {}