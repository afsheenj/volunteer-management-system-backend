package com.vserve.project.controller;

import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.PublicProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/profile")
public class PublicProfileController {

    private final PublicProfileService publicProfileService;

    public PublicProfileController(PublicProfileService publicProfileService) {
        this.publicProfileService = publicProfileService;
    }

    @GetMapping("/{name}")
    public ResponseEntity<ApiResponse<?>> getPublicProfile(
            @PathVariable(name = "name") String name) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Public profile fetched",
                        publicProfileService.getPublicProfile(name)
                )
        );
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<?>> getPublicProfileById(
            @PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Public profile fetched",
                        publicProfileService.getPublicProfileById(id)
                )
        );
    }
}
