package com.vserve.project.controller;


import com.vserve.project.dto.ParticipationResponseDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.ParticipationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ParticipationController {


    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }

    @GetMapping("/public/participations/service/{serviceId}")
    public ResponseEntity<ApiResponse<Page<ParticipationResponseDto>>> getParticipants(
            @PathVariable("serviceId") Long serviceId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "participantType", required = false) String participantType,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Participants fetched successfully",
                        participationService.getParticipants(serviceId, status, participantType, pageNumber, pageSize)
                )
        );
    }
}
