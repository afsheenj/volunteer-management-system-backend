package com.vserve.project.controller;

import com.vserve.project.response.ApiResponse;
import com.vserve.project.service.OtpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/user/otp/send")
    public ResponseEntity<ApiResponse<String>> sendUserOtp(
            @RequestParam String email) {
        otpService.sendUserOtp(email);
        return ResponseEntity.ok(
                ApiResponse.ok("OTP sent successfully", null));
    }

    @PostMapping("/user/otp/verify")
    public ResponseEntity<ApiResponse<String>> verifyUserOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        return ResponseEntity.ok(
                ApiResponse.ok("OTP Verification Done",
                        otpService.verifyUserOtp(email, otp)
                ));
    }

    @PostMapping("/organization/otp/send")
    public ResponseEntity<ApiResponse<String>> sendOrgOtp(
            @RequestParam String email) {
        otpService.sendOrgOtp(email);
        return ResponseEntity.ok(
                ApiResponse.ok("Organization OTP sent", null));
    }

    @PostMapping("/organization/otp/verify")
    public ResponseEntity<ApiResponse<String>> verifyOrgOtp(
            @RequestParam String email,
            @RequestParam String otp) {
        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Organization Verification Done",
                        otpService.verifyOrgOtp(email, otp)
                ));
    }
}
