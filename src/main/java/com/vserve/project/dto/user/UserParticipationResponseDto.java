package com.vserve.project.dto.user;

import com.vserve.project.enums.ParticipationStatus;

import java.time.LocalDateTime;

public record UserParticipationResponseDto(
        Long id,
        Long serviceId,
        String serviceTitle,
        Long userId,
        String username,
        ParticipationStatus status,
        LocalDateTime appliedAt
) { }
