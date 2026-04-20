package com.vserve.project.controller;

import com.vserve.project.dto.ParticipationResponseDto;
import com.vserve.project.dto.user.UserAttendanceDto;
import com.vserve.project.dto.user.UserParticipationDto;
import com.vserve.project.dto.user.UserParticipationResponseDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.ParticipationService;
import com.vserve.project.service.UserParticipationService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/participations")
public class UserParticipationController {

    private final UserParticipationService userParticipationService;
    private final ParticipationService participationService;

    public UserParticipationController(UserParticipationService userParticipationService, ParticipationService participationService) {
        this.userParticipationService = userParticipationService;
        this.participationService = participationService;
    }



    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserParticipationResponseDto>> register(
            @RequestBody UserParticipationDto dto
    ) {
        UserParticipationResponseDto response =
                userParticipationService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registered successfully", response));
    }


    @PutMapping("/approve")
    public ResponseEntity<ApiResponse<UserParticipationResponseDto>> approve(
            @RequestBody UserParticipationDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(
                        "Approved Successfully",
                        userParticipationService.approve(dto.serviceId(),dto.userId())
                ));
    }

    @PutMapping("/reject")
    public ResponseEntity<ApiResponse<UserParticipationResponseDto>> reject(
            @RequestBody UserParticipationDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(
                        "Rejected",
                        userParticipationService.reject(dto.serviceId(),dto.userId())
                ));
    }


    @PutMapping("/withdraw")
    public ResponseEntity<ApiResponse<UserParticipationResponseDto>> withdraw(
          @RequestBody UserParticipationDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(
                        "Withdrawn Successfully",
                        userParticipationService.withdraw(dto.serviceId(),dto.userId())
                ));
    }


    @PutMapping("/attendance")
    public ResponseEntity<ApiResponse<UserParticipationResponseDto>> markAttendance(
            @RequestBody UserAttendanceDto userAttendanceDto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok(
                        "Attendance marked successfully",
                        userParticipationService.markAttendance(userAttendanceDto.serviceId(), userAttendanceDto.userId(),userAttendanceDto)
                ));
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<ApiResponse<Page<UserParticipationResponseDto>>>
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
                        userParticipationService.getByServiceRequest(
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<UserParticipationResponseDto>>> getByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "appliedAt") String sortBy,
            @RequestParam(name = "order", defaultValue = "desc") String order
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "User participations fetched successfully",
                        userParticipationService.getByUser(
                                userId,
                                pageNumber,
                                pageSize,
                                sortBy,
                                order
                        )
                )
        );
    }


}