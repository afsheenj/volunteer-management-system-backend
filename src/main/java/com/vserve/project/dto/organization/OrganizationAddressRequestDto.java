package com.vserve.project.dto.organization;

public record OrganizationAddressRequestDto(

        Long organization_Id,
        String state,
        String city

) {}
