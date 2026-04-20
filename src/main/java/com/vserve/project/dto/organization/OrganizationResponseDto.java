package com.vserve.project.dto.organization;

import com.vserve.project.enums.AccountStatus;

public record OrganizationResponseDto(
        Long id,
        String organizationName,
        String email,
        String phone,
        AccountStatus status,
        String description,
        int totalMembers
) {
}
