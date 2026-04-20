package com.vserve.project.controller;


import com.vserve.project.dto.user.UserAddressDto;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAddressController {
    private final UserAddressService userAddressService;


    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @PostMapping("/address")
    public ResponseEntity<ApiResponse<String>> userAddress(
            @RequestBody @Valid UserAddressDto dto)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "User Address was added",
                        userAddressService.userAddress(dto)
                ));
    }

    @GetMapping("/address/{userId}")
    public ResponseEntity<ApiResponse<UserAddressDto>> getUserAddress(
            @PathVariable("userId") Long userId) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "User address fetched successfully",
                        userAddressService.getUserAddress(userId)
                ));
    }

    @PutMapping("/address")
    public ResponseEntity<ApiResponse<String>> updateUserAddress(
            @RequestBody @Valid UserAddressDto dto
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.ok(
                        "User address was updated successfully",
                        userAddressService.updateUserAddress(dto)
                )
        );
    }
}
