package com.vserve.project.controller;

import com.vserve.project.dto.organization.OrganizationHistoryDto;
import com.vserve.project.dto.user.UserHistoryDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.HistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<UserHistoryDto>>> getUserHistory(
            @PathVariable("userId") Long userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "User history fetched successfully",
                        historyService.getUserHistory(userId)
                )
        );
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<OrganizationHistoryDto>>> getOrganizationHistory(
            @PathVariable("organizationId") Long organizationId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization history fetched successfully",
                        historyService.getOrganizationHistory(organizationId)
                )
        );
    }

}