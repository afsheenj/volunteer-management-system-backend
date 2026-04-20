package com.vserve.project.dto.organization;

import java.time.LocalDateTime;

public record OrganizationRegisterRequestDto(
        String organizationName,
        String email,
        String phone,
        String password,
        String confirmPassword,
        String description
) {}