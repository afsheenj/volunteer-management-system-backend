package com.vserve.project.controller;

import com.vserve.project.dto.CommonServiceRequestResponseDto;
import com.vserve.project.service.PostedRequestService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/requests")
public class PostedRequestController {
    private final PostedRequestService service;

    public PostedRequestController(PostedRequestService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CommonServiceRequestResponseDto>> getAllRequests(
            @RequestParam(name="page",defaultValue = "0") int page,
            @RequestParam(name="size",defaultValue = "15") int size) {

        return ResponseEntity.ok(service.getAllRequests(page, size));
    }
}
