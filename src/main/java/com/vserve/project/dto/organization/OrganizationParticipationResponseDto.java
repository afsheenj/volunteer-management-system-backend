package com.vserve.project.dto.organization;

import com.vserve.project.enums.ParticipationStatus;

import java.time.LocalDateTime;

public record OrganizationParticipationResponseDto(
        Long id,
        Long serviceId,
        String serviceTitle,
        Long organizationId,
        String organizationName,
        String status,
        LocalDateTime appliedAt,
        Integer memberCount
) {
}
