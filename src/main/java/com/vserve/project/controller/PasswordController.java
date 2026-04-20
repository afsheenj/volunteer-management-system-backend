package com.vserve.project.controller;

import com.vserve.project.dto.PasswordUpdateRequestDto;
import com.vserve.project.service.PasswordService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/password")
public class PasswordController {

    private final PasswordService passwordService;

    public PasswordController(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PutMapping("/update")
    public String updatePassword(
            @RequestBody PasswordUpdateRequestDto dto) {

        return passwordService.updatePassword(dto);
    }

}
