package com.vserve.project.controller;


import com.vserve.project.dto.ParticipationResponseDto;
import com.vserve.project.dto.organization.OrganizationAttendanceDto;
import com.vserve.project.dto.organization.OrganizationParticipationDto;
import com.vserve.project.dto.organization.OrganizationParticipationResponseDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationParticipationService;
import com.vserve.project.service.ParticipationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/organization/participations")
public class OrganizationParticipationController {

    private final OrganizationParticipationService organizationParticipationService;
    private final ParticipationService participationService;

    public OrganizationParticipationController(OrganizationParticipationService organizationParticipationService, ParticipationService participationService) {
        this.organizationParticipationService = organizationParticipationService;
        this.participationService = participationService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>> register(
            @RequestBody OrganizationParticipationDto dto
            ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok("Registered successfully", organizationParticipationService.register(dto))
        );
    }

    @PutMapping("/approve")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>>
    approve(@RequestBody OrganizationParticipationDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok("Approved",
                        organizationParticipationService.approve(dto))
        );
    }
    @PutMapping("/reject")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>>
    reject(@RequestBody OrganizationParticipationDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok("Rejected",
                        organizationParticipationService.reject(dto))
        );
    }
    @PutMapping("/withdraw")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>>
    withdraw(@RequestBody OrganizationParticipationDto dto
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok("Withdrawed successfully",
                        organizationParticipationService.withdraw(dto))
        );
    }

    @PutMapping("/attendance")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>>
    markAttendance( @RequestBody OrganizationAttendanceDto organizationAttendanceDto) {

        return ResponseEntity.ok(
                ApiResponse.ok("Attended",
                        organizationParticipationService.markAttendance(organizationAttendanceDto)
        ));
    }

    @PutMapping("/complete")
    public ResponseEntity<ApiResponse<OrganizationParticipationResponseDto>>
    complete(@RequestBody OrganizationParticipationDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok("Completed successfully",
                        organizationParticipationService.complete(dto))
        );
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ApiResponse<Page<OrganizationParticipationResponseDto>>>
    getByServiceRequest(
            @PathVariable("serviceId") Long serviceId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "appliedAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Participants fetched successfully",
                        organizationParticipationService.getByServiceRequest(
                                serviceId,
                                status,
                                pageNumber,
                                pageSize,
                                sortBy,
                                order
                        )
                )
        );
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<ApiResponse<Page<OrganizationParticipationResponseDto>>>
    getByOrganization(
            @PathVariable("orgId") Long orgId,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization participations fetched successfully",
                        organizationParticipationService.getByOrganization(orgId, pageNumber, pageSize)
                )
        );
    }

}
