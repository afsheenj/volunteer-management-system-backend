package com.vserve.project.dto.organization;

public record OrganizationDashboardDto(
        long activeRequests,
        long upcomingRequests,
        long totalVolunteersNeeded
) {
}
