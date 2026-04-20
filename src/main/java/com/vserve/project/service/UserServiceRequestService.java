package com.vserve.project.service;

import com.vserve.project.dto.CommonServiceRequestResponseDto;
import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
public interface UserServiceRequestService {

    String createRequest(Long userId, CreateServiceRequestDto dto);

    Page<ServiceRequestResponseDto> getUserRequests(Long userId, int page, int size);
    String updateUserRequest(Long userId, Long requestId, CreateServiceRequestDto dto);
    String deleteUserRequest(Long userId, Long requestId);

    Page<ServiceRequestResponseDto> getRequestsForVolunteer(Long userId, int page, int size);

    ServiceRequestEditDto getRequestById(Long requestId);

    String cancelRequest(Long id);

    Page<CommonServiceRequestResponseDto> aiSearch(int page, int size, String query, String location, String serviceType, LocalDate date);
}
