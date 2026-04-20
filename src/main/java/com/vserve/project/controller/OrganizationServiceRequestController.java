package com.vserve.project.controller;

import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import com.vserve.project.service.OrganizationServiceRequestService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/requests/organizations/{orgId}")
public class OrganizationServiceRequestController {
    private final OrganizationServiceRequestService service;

    public OrganizationServiceRequestController(OrganizationServiceRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> createRequest(
            @PathVariable Long orgId,
            @RequestBody CreateServiceRequestDto dto) {

        return ResponseEntity.ok(service.createRequest(orgId, dto));
    }

    @GetMapping
    public ResponseEntity<Page<ServiceRequestResponseDto>> getOrgRequests(
            @PathVariable("orgId") Long orgId,
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(service.getOrgRequests(orgId, page, size));
    }

    @PutMapping("/{requestId}")
    public ResponseEntity<String> updateOrganizationRequest(
            @PathVariable Long orgId,
            @PathVariable Long requestId,
            @RequestBody CreateServiceRequestDto dto) {

        return ResponseEntity.ok(service.updateOrganizationRequest(orgId, requestId, dto));
    }

    @DeleteMapping("/{requestId}")
    public ResponseEntity<String> deleteOrganizationRequest(
            @PathVariable Long orgId,
            @PathVariable Long requestId) {

        return ResponseEntity.ok(service.deleteOrganizationRequest(orgId, requestId));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long orgId,@PathVariable Long id){
        return ResponseEntity.ok(service.cancelRequest(orgId,id));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ServiceRequestEditDto> getRequestDetails(
            @PathVariable Long orgId,
            @PathVariable Long requestId) {

        return ResponseEntity.ok(
                service.getRequestDetails(orgId, requestId)
        );
    }

}
