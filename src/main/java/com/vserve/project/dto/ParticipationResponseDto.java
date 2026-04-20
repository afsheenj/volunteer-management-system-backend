package com.vserve.project.dto;

import java.time.LocalDateTime;

public record ParticipationResponseDto(

        Long id,
        Long serviceId,
        String serviceTitle,

        Long participantId,
        String participantName,
        String participantType,

        String status,
        LocalDateTime appliedAt,
        Integer memberCount

) {}