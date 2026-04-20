package com.vserve.project.service;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationMemberCreateDto;
import com.vserve.project.dto.organization.OrganizationRegisterRequestDto;
import com.vserve.project.dto.organization.OrganizationResponseDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface OrganizationService {

    String registerOrganization(OrganizationRegisterRequestDto dto);

    Page<OrganizationResponseDto> getAllRegisteredOrganizations(int page, int size, String sortBy, String order, String search,String statua);

    String addMember(Long orgId, @Valid OrganizationMemberCreateDto dto);

    String updateOrganization(Long id ,OrganizationRegisterRequestDto dto);

    Page<MemberDto> getAllMembers(
            Long orgId,
            int page,
            int size,
            String sortBy,
            String order,
            String search
    );

    String updateMemberDetails(Long memberId, OrganizationMemberCreateDto dto);

    String removeMember(Long memberId);

    String updateOrganizationStatus(Long id, String status);

    String deleteOrganization(Long id);
}
