package com.vserve.project.service;

import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.entity.OrganizationAddress;

public interface OrganizationAddressService {
    String registerAddress(OrganizationAddressRequestDto dto) ;

    String updateOrganizationAddress(OrganizationAddressRequestDto dto);

    OrganizationAddressRequestDto getOrganizationDetails(Long organizationId);
}
