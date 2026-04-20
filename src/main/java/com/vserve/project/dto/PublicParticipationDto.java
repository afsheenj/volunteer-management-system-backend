package com.vserve.project.dto;

public record PublicParticipationDto(
        Long requestId,
        String requestTitle,
        String participationStatus
) {}