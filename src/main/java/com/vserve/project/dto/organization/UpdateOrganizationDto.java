package com.vserve.project.dto.organization;

public record UpdateOrganizationDto(
        String organizationName,
        String email,
        String phone,
        String description
) {
}
