package com.vserve.project.service;

import com.vserve.project.dto.ParticipationResponseDto;
import org.springframework.data.domain.Page;

public interface ParticipationService {
    Page<ParticipationResponseDto> getParticipants(Long serviceId, String status,String participantType, Integer pageNumber, Integer pageSize);
    ParticipationResponseDto approve(Long serviceId, Long participantId, String participantType);

    ParticipationResponseDto reject(Long serviceId, Long participantId, String participantType);
}
