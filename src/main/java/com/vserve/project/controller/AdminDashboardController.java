package com.vserve.project.controller;

import com.vserve.project.dto.admin.*;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.AdminDashboardService;
import com.vserve.project.service.PostedRequestService;
import com.vserve.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {
    private final AdminDashboardService dashboardService;
    private final PostedRequestService postedRequestService;
    private final UserService userService;

    public AdminDashboardController(AdminDashboardService dashboardService, PostedRequestService postedRequestService, UserService userService) {
        this.dashboardService = dashboardService;
        this.postedRequestService = postedRequestService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardDto>> getDashboard() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.ok("Dashboard data fetched",dashboardService.getDashboardData())

        );

    }

    @GetMapping("/all-requests")
    public ResponseEntity<Page<AdminServiceRequestDto>> getAllRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        return ResponseEntity.ok(
                dashboardService.getAllRequestsByAdmin(page, size, category, state, city, sortBy, order)
        );
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {

        postedRequestService.deleteRequest(id);
        return ResponseEntity.ok("Request deleted successfully");
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<AdminUserResponseDto>>> getAllUsers(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean availability
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Users fetched successfully",
                        userService.getAllUsers(page, size, username, role, availability)
                )
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AdminProfileDto>> getAdminProfile(
            @RequestParam(name = "id") Long id
    ) {
        return ResponseEntity.ok(
                ApiResponse.ok("Admin profile fetched", userService.getAdminProfile(id))
        );
    }

    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<AdminProfileDto>>> getAllAdmins() {
        return ResponseEntity.ok(
                ApiResponse.ok("Admins fetched", userService.getAllAdmins())
        );
    }

    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<String>> createAdmin(
            @RequestBody @Valid CreateAdminDto dto
    ) {
        userService.createAdmin(dto);
        return ResponseEntity.ok(ApiResponse.ok("Admin created & email sent", null));
    }
}
