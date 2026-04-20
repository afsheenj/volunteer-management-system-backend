package com.vserve.project.controller;

import com.vserve.project.dto.organization.OrganizationDashboardDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationDashboardController {

    private final OrganizationDashboardService organizationDashboardService;

    public OrganizationDashboardController(OrganizationDashboardService organizationDashboardService) {
        this.organizationDashboardService = organizationDashboardService;
    }

    @GetMapping("/{orgId}/dashboard/stats")
    public ResponseEntity<ApiResponse<OrganizationDashboardDto>> getStats(@PathVariable Long orgId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(
                "Organization dashboard stats fetched",
                organizationDashboardService.getDashboardStats(orgId)
        ));
    }
}