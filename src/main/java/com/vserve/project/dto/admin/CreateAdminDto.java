package com.vserve.project.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminDto(

        @NotBlank
        String username,

        @Email
        @NotBlank
        String email,

        String phone
) {}