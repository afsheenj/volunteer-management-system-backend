package com.vserve.project.controller;

import com.vserve.project.dto.organization.OrganizationAddressRequestDto;
import com.vserve.project.dto.organization.OrganizationRegisterRequestDto;
import com.vserve.project.entity.OrganizationAddress;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationAddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations/address")
public class OrganizationAddressController {
    public final OrganizationAddressService organizationAddressService;

    public OrganizationAddressController(OrganizationAddressService organizationAddressService) {
        this.organizationAddressService = organizationAddressService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerAddress(@RequestBody OrganizationAddressRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Address Registered"
                        , organizationAddressService.registerAddress(dto)
                ));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateOrganizationAddress(

            @RequestBody OrganizationAddressRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(
                "Organization details Updated Successfully",
                organizationAddressService.updateOrganizationAddress(dto)));
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationAddressRequestDto>> getOrganizationAddress(
            @PathVariable("organizationId") Long organizationId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok("Organization details fetched",
                        organizationAddressService.getOrganizationDetails(organizationId)
        ));
    }
}