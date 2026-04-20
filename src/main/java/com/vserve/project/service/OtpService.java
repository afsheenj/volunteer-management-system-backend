package com.vserve.project.service;

public interface OtpService {

    void sendUserOtp(String email);

    String verifyUserOtp(String email, String otp);

    void sendOrgOtp(String email);

    String verifyOrgOtp(String email, String otp);
}
