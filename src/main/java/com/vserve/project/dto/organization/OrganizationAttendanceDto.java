package com.vserve.project.dto.organization;

public record OrganizationAttendanceDto(
        Long serviceId,
        Long organizationId,
        boolean attended) {
}
