package com.vserve.project.service;

import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import org.springframework.data.domain.Page;

public interface OrganizationServiceRequestService {

    String createRequest(Long orgId, CreateServiceRequestDto dto);

    Page<ServiceRequestResponseDto> getOrgRequests(Long orgId, int page, int size);

    String updateOrganizationRequest(Long orgId, Long requestId, CreateServiceRequestDto dto);

    String deleteOrganizationRequest(Long orgId, Long requestId);

    String cancelRequest(Long orgId,Long id);

    ServiceRequestEditDto getRequestDetails(Long orgId, Long requestId);
}
