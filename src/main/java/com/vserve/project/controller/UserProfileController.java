package com.vserve.project.controller;

import com.vserve.project.dto.user.*;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile/user")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/me/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getUserProfile(@PathVariable(name = "id") Long id){
        return ResponseEntity.status(HttpStatus.OK).
                body(ApiResponse.ok("Profile fetched successfully",
                        userProfileService.getUserProfile(id)));

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @PathVariable(name = "id") Long id,
            @RequestBody UserProfileUpdateDto dto) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Profile updated successfully",
                        userProfileService.updateUserProfile(id, dto)
                )
        );
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<ApiResponse<UserAddressDto>> getUserAddress(
            @PathVariable(name = "id") Long id) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Address fetched successfully",
                        userProfileService.getUserAddress(id)
                )
        );
    }

    @GetMapping("/skills/{userId}")
    public ResponseEntity<ApiResponse<List<UserSkillResponseDto>>> getUserSkills(
            @PathVariable(name = "userId") Long userId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "User skills fetched successfully",
                        userProfileService.getUserSkills(userId)
                )
        );

    }

    @GetMapping("/public/{userId}")
    public ResponseEntity<ApiResponse<UserPublicProfileDto>> getPublicProfile(
            @PathVariable(name = "userId") Long userId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Public profile fetched successfully",
                        userProfileService.getPublicProfile(userId)
                )
        );
    }




}
