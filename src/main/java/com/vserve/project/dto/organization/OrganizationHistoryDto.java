package com.vserve.project.dto.organization;

import com.vserve.project.dto.ParticipationResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record OrganizationHistoryDto(
        Long serviceId,
        String title,
        String description,
        String category,
        String location,
        String requestType,
        String requestStatus,
        Long userId,          // member ID if participation
        String username,      // member name if participation
        String participationStatus, // null for posted
        Boolean feedbackGiven,      // null for posted
        Integer rating,             // null for posted
        String comment,             // null for posted
        LocalDateTime appliedAt,    // null for posted
        LocalDateTime createdAt,    // service request created date
        List<ParticipationResponseDto> participants
) {
}
