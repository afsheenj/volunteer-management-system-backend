package com.vserve.project.controller;

import com.vserve.project.dto.user.UserDashboardDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.UserDashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserDashboardController {
    private final UserDashboardService dashboardService;

    public UserDashboardController(UserDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/users/{userId}/dashboard/stats")
    public ResponseEntity<ApiResponse<UserDashboardDto>> getStats(
            @PathVariable Long userId,
            @RequestParam String role
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.ok(
                "Dashboard stats fetched",
                dashboardService.getStats(userId, role)
        ));
    }
}
