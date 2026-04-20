package com.vserve.project.dto.organization;

public record OrganizationMemberCreateDto(
        Long organizationId,
        String username,
        String email,
        String phone
) {}
