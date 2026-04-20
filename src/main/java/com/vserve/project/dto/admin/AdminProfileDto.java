package com.vserve.project.dto.admin;

public record AdminProfileDto(
        Long id,
        String username,
        String email,
        String phone
) {}