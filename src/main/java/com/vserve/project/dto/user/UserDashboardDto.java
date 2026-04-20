package com.vserve.project.dto.user;

public record UserDashboardDto(
        int totalVolunteersNeeded,
        int openRequests,
        int upcomingRequests
) {
}
