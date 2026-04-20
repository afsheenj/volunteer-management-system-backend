package com.vserve.project.service;

import com.vserve.project.dto.user.UserAttendanceDto;
import com.vserve.project.dto.user.UserParticipationDto;
import com.vserve.project.dto.user.UserParticipationResponseDto;
import org.springframework.data.domain.Page;

public interface UserParticipationService {

    UserParticipationResponseDto register(UserParticipationDto dto);

    UserParticipationResponseDto approve(Long serviceId, Long userId);

    UserParticipationResponseDto reject(Long serviceId, Long userId);

    UserParticipationResponseDto withdraw(Long serviceId, Long userId);

    UserParticipationResponseDto markAttendance(Long serviceId, Long userId, UserAttendanceDto dto);

    UserParticipationResponseDto complete(Long serviceId, Long userId);


    Page<UserParticipationResponseDto>
    getByServiceRequest(
            Long serviceId,
            String status,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order);

    Page<UserParticipationResponseDto> getByUser(
            Long userId,
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String order
    );
}