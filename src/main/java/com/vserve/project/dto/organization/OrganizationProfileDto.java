package com.vserve.project.dto.organization;

import com.vserve.project.enums.AccountStatus;

import java.time.LocalDateTime;

public record OrganizationProfileDto(
        Long id,
        String name,
        String email,
        String phone,
        long memberCount,
        String description,
        Double score
) {
}
