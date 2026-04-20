package com.vserve.project.service;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.dto.organization.OrganizationProfileDto;
import com.vserve.project.dto.organization.UpdateOrganizationDto;

import java.util.List;

public interface OrganizationProfileService {
    OrganizationProfileDto getOrganizationProfile(Long orgId);
    OrganizationAddressRequestDto getOrganizationAddress(Long organizationId);
    List<MemberDto> getOrganizationMembers(Long organizationId);
    String updateOrganizationProfile(Long orgId, UpdateOrganizationDto dto);
}
