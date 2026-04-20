package com.vserve.project.controller;

import com.vserve.project.dto.organization.MemberDto;
import com.vserve.project.dto.organization.OrganizationMemberCreateDto;
import com.vserve.project.dto.organization.OrganizationRegisterRequestDto;
import com.vserve.project.dto.organization.OrganizationResponseDto;
import com.vserve.project.entity.User;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OrganizationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class  OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/public/organizations/register")
    public ResponseEntity<ApiResponse<String>> registerOrganization(
            @RequestBody @Valid OrganizationRegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Organization Registered Successfully",
                        organizationService.registerOrganization(dto)
                ));
    }

    @GetMapping("/public/organizations/list")
    public ResponseEntity<ApiResponse<Page<OrganizationResponseDto>>> getAllRegisteredOrganizations(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "6") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "desc") String order,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok(
                        "Fetched registered organizations with pagination",
                        organizationService.getAllRegisteredOrganizations(page, size, sortBy, order, search,status)
                )
        );
    }

    @PostMapping("/organizations/{orgId}/members")
    public ResponseEntity<ApiResponse<String>> addMember(
            @PathVariable(name = "orgId") Long orgId,
            @RequestBody @Valid OrganizationMemberCreateDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Member added successfully",
                        organizationService.addMember(orgId, dto)
                )
        );
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateOrganization(
        @PathVariable(name = "id") Long id,
        @RequestBody OrganizationRegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(
            "Organization details Updated Successfully",
            organizationService.updateOrganization(id,dto)));
    }


    @GetMapping("organizations/{orgId}/members")
    public ResponseEntity<ApiResponse<Page<MemberDto>>> getAllMembers(
            @PathVariable(name = "orgId") Long orgId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "asc") String order,
            @RequestParam(name = "search", required = false) String search
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok(
                        "Fetched all members",
                        organizationService.getAllMembers(orgId,page,size,sortBy, order,search)
                )
        );
    }

    @PutMapping("organizations/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> updateMemberDetails(
            @PathVariable(name = "memberId") Long memberId,
            @RequestBody OrganizationMemberCreateDto dto
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok("Datas updated", organizationService.updateMemberDetails(memberId,dto))
        );
    }

    @DeleteMapping("organizations/members/{memberId}")
    public ResponseEntity<ApiResponse<String>> removeMember(
            @PathVariable(name = "memberId") Long memberId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok("Member was removed",organizationService.removeMember(memberId))
        );
    }

    @PutMapping("/admin/organizations/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateOrganizationStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok(
                        "Organization status updated",
                        organizationService.updateOrganizationStatus(id, status)
                )
        );
    }

    @DeleteMapping("/admin/organizations/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrganization(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization deleted",
                        organizationService.deleteOrganization(id)
                )
        );
    }
}

