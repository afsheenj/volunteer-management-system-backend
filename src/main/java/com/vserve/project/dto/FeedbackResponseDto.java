package com.vserve.project.dto;

import java.time.LocalDateTime;

public record FeedbackResponseDto(
        Long id,
        Long serviceId,
        Long userParticipationId,
        Long organizationParticipationId,
        Integer rating,
        String comment,
        String givenBy,
        LocalDateTime createdAt
) {}
