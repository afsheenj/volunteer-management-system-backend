package com.vserve.project.controller;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.dto.organization.OrganizationProfileDto;
import com.vserve.project.dto.organization.UpdateOrganizationDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations/profile")
public class OrganizationProfileController {

    private final OrganizationProfileService organizationProfileService;

    public OrganizationProfileController(OrganizationProfileService organizationProfileService) {
        this.organizationProfileService = organizationProfileService;
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<OrganizationProfileDto>> getOrganizationProfile(
            @PathVariable("orgId") Long orgId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization profile fetched successfully",
                        organizationProfileService.getOrganizationProfile(orgId)
                )
        );
    }

    @PutMapping("/{orgId}")
    public ResponseEntity<ApiResponse> updateOrganizationProfile(
            @PathVariable("orgId") Long orgId,
            @RequestBody UpdateOrganizationDto dto) {

        String message = organizationProfileService.updateOrganizationProfile(orgId, dto);

        return ResponseEntity.ok(
                new ApiResponse(true, message, null)
        );
    }


    @GetMapping("/{organization_Id}/address")
    public ResponseEntity<ApiResponse<OrganizationAddressRequestDto>> getOrganizationAddress(
            @PathVariable("organization_Id") Long organizationId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization address fetched successfully",
                        organizationProfileService.getOrganizationAddress(organizationId)
                )
        );
    }

    @GetMapping("/{organization_Id}/members")
    public ResponseEntity<ApiResponse<List<MemberDto>>> getOrganizationMembers(
            @PathVariable("organization_Id") Long organizationId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization members fetched successfully",
                        organizationProfileService.getOrganizationMembers(organizationId)
                )
        );
    }
}
