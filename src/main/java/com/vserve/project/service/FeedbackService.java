package com.vserve.project.service;

import com.vserve.project.dto.CreateFeedbackDto;
import com.vserve.project.dto.FeedbackResponseDto;

import java.util.List;

public interface FeedbackService {

    FeedbackResponseDto createFeedback(CreateFeedbackDto dto);

    List<FeedbackResponseDto> getByUserParticipation(Long id);

    List<FeedbackResponseDto> getByOrganizationParticipation(Long id);

    List<FeedbackResponseDto> getByGivenByUser(Long userId);

    List<FeedbackResponseDto> getByGivenByOrganization(Long orgId);
}
