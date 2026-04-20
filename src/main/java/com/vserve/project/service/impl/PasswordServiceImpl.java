package com.vserve.project.service.impl;

import com.vserve.project.dto.PasswordUpdateRequestDto;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.Otp;
import com.vserve.project.entity.User;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.OtpRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.PasswordService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OtpRepository otpRepository;

    public PasswordServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, OtpRepository otpRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.otpRepository = otpRepository;
    }

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public String updatePassword(PasswordUpdateRequestDto dto) {
        String email = dto.email();
        User user = userRepository.findByEmail(email);
        Organization org = null;
        Otp otp = null;

        if (user != null) {
            otp = otpRepository.findLatestOtp(user);
        } else {
            org = organizationRepository.findByEmail(email);

            if (org != null) {
                otp = otpRepository.findLatestOrganizationOtp(org);
            } else {
                throw new BusinessException("Email not registered");
            }
        }

        if (otp == null)
            throw new BusinessException("OTP not found");

        if (otp.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new BusinessException("OTP expired");

        if (!otp.getUsed())
            throw new BusinessException("OTP not verified");

        if (user != null) {
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
            userRepository.save(user);
            return "User password updated successfully";
        }

        org.setPassword(passwordEncoder.encode(dto.newPassword()));
        organizationRepository.save(org);
        return "Organization password updated successfully";
    }

}
