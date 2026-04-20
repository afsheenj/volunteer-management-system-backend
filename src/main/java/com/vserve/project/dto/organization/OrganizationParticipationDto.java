package com.vserve.project.dto.organization;

public record OrganizationParticipationDto(Long serviceId,
                                           Long organizationId,
                                           Integer memberCount) {
}
