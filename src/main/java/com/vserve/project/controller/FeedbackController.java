package com.vserve.project.controller;

import com.vserve.project.dto.CreateFeedbackDto;
import com.vserve.project.dto.FeedbackResponseDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> createFeedback(
            @RequestBody CreateFeedbackDto dto
    ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "Feedback submitted successfully",
                        feedbackService.createFeedback(dto)
                ));
    }

    @GetMapping("/user-participation/{id}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getByUserParticipation(
            @PathVariable(name = "id") Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Feedback fetched",
                        feedbackService.getByUserParticipation(id)
                )
        );
    }

    @GetMapping("/organization-participation/{id}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getByOrganizationParticipation(
            @PathVariable(name = "id") Long id
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Feedback fetched",
                        feedbackService.getByOrganizationParticipation(id)
                )
        );
    }

    @GetMapping("/given-by-user/{userId}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getByGivenByUser(
            @PathVariable(name = "userId") Long userId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Feedback fetched",
                        feedbackService.getByGivenByUser(userId)
                )
        );
    }

    @GetMapping("/given-by-organization/{orgId}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getByGivenByOrganization(
            @PathVariable(name = "orgId") Long orgId
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Feedback fetched",
                        feedbackService.getByGivenByOrganization(orgId)
                )
        );
    }
}
