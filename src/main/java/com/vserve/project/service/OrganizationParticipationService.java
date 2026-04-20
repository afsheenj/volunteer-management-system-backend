package com.vserve.project.service;

import com.vserve.project.dto.organization.OrganizationAttendanceDto;
import com.vserve.project.dto.organization.OrganizationParticipationDto;
import com.vserve.project.dto.organization.OrganizationParticipationResponseDto;
import org.springframework.data.domain.Page;

public interface OrganizationParticipationService {

   OrganizationParticipationResponseDto register(OrganizationParticipationDto dto);

   OrganizationParticipationResponseDto approve(OrganizationParticipationDto dto);

   OrganizationParticipationResponseDto reject(OrganizationParticipationDto dto);

   OrganizationParticipationResponseDto withdraw(OrganizationParticipationDto dto);

   OrganizationParticipationResponseDto markAttendance(
           OrganizationAttendanceDto dto);

   OrganizationParticipationResponseDto complete(OrganizationParticipationDto dto);

   Page<OrganizationParticipationResponseDto> getByServiceRequest(
           Long serviceId,
           String status,
           Integer pageNumber,
           Integer pageSize,
           String sortBy,
           String order);

    Page<OrganizationParticipationResponseDto> getByOrganization(Long orgId, Integer pageNumber, Integer pageSize);
}
