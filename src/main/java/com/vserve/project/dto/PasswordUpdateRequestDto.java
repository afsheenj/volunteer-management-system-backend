package com.vserve.project.dto;

public record PasswordUpdateRequestDto (
        String email,
        String newPassword
){ }
