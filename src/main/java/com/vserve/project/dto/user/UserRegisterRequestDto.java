package com.vserve.project.dto.user;

import com.vserve.project.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record  UserRegisterRequestDto(
        String username,
        @Email(message = "Invalid Email Format")
        String email,
        @NotNull(message = "Phone no must")
        String phone,
        String password,
        String confirmPassword,
        Role role,
        String bio
) {}
