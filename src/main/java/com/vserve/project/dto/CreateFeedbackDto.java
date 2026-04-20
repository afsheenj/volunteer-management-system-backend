package com.vserve.project.dto;

public record CreateFeedbackDto(
        Long userParticipationId,
        Long organizationParticipationId,
        Long givenByUserId,
        Long givenByOrganizationId,
        Integer rating,
        String comment
) {}
