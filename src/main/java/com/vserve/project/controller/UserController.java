package com.vserve.project.controller;

import com.vserve.project.dto.user.UserAddressDto;
import com.vserve.project.dto.user.UserRegisterRequestDto;
import com.vserve.project.entity.User;
import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/public/users/register")
    public ResponseEntity<ApiResponse<String>> registerUser(
            @RequestBody @Valid UserRegisterRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(
                        "User Registered Successfully",
                        userService.registerUser(dto)
                ));
    }
    @PutMapping("/users/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateUser(@PathVariable("id") Long id ,
                                                          @RequestBody UserRegisterRequestDto dto){
        return ResponseEntity.status(HttpStatus.OK).
                body(ApiResponse.ok(
                        "User updated Successfully",
                        userService.updateUser(id,dto)));
    }

}
