package com.vserve.project.controller;

import com.vserve.project.dto.CommonServiceRequestResponseDto;
import com.vserve.project.dto.CreateServiceRequestDto;
import com.vserve.project.dto.ServiceRequestEditDto;
import com.vserve.project.dto.ServiceRequestResponseDto;
import com.vserve.project.service.UserServiceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserServiceRequestController {


    private final UserServiceRequestService service;

    public UserServiceRequestController(UserServiceRequestService service) {
        this.service = service;
    }

    @PostMapping("/requests/users/{userId}")
    public ResponseEntity<String> createRequest(
            @PathVariable("userId") Long userId,
            @RequestBody CreateServiceRequestDto dto) {

        return ResponseEntity.ok(service.createRequest(userId, dto));
    }

    @GetMapping("/requests/users/{userId}")
    public ResponseEntity<Page<ServiceRequestResponseDto>> getUserRequests(
            @PathVariable("userId") Long userId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {

        return ResponseEntity.ok(service.getUserRequests(userId, page, size));
    }

    @GetMapping("/public/requests/id/{requestId}")
    public ResponseEntity<ServiceRequestEditDto> getRequestByIdPublic(
            @PathVariable("requestId") Long requestId) {

        return ResponseEntity.ok(service.getRequestById(requestId));
    }
    @GetMapping("/requests/id/{requestId}")
    public ResponseEntity<ServiceRequestEditDto> getRequestById(
            @PathVariable("requestId") Long requestId) {

        return ResponseEntity.ok(service.getRequestById(requestId));
    }


    @PutMapping("/requests/users/{userId}/{requestId}")
    public ResponseEntity<String> updateUserRequest(
            @PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId,
            @RequestBody CreateServiceRequestDto dto) {

        return ResponseEntity.ok(service.updateUserRequest(userId, requestId, dto));
    }

    @DeleteMapping("/requests/users/{userId}/{requestId}")
    public ResponseEntity<String> deleteUserRequest(
            @PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {

        return ResponseEntity.ok(service.deleteUserRequest(userId, requestId));
    }

    @GetMapping("/volunteer")
    public ResponseEntity<Page<ServiceRequestResponseDto>> getVolunteerRequests(
            @PathVariable Long userId,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size){
        return ResponseEntity.ok(service.getRequestsForVolunteer(userId, page, size));
    }

    @PutMapping ("/requests/users/{id}/cancel")
    public ResponseEntity<String> cancelRequest(@PathVariable Long id){
        service.cancelRequest(id);
        return ResponseEntity.ok("Request Cancelled Successfully");
    }


    @GetMapping("/public/requests/ai-search")
    public ResponseEntity<Page<CommonServiceRequestResponseDto>> aiSearch(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) LocalDate date) {

        return ResponseEntity.ok(
                service.aiSearch(
                        page,
                        size,
                        query == null ? "" : query,
                        location == null ? "" : location,
                        serviceType == null ? "" : serviceType,
                        date
                )
        );
    }

}