package com.vserve.project.service.impl;

import com.vserve.project.entity.Organization;
import com.vserve.project.entity.Otp;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.OtpRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.OtpService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OtpRepository otpRepository;
    private final JavaMailSender javaMailSender;

    public OtpServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, OtpRepository otpRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.otpRepository = otpRepository;
        this.javaMailSender = javaMailSender;
    }

    private void sendMail(String mailId, String subject, String body) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailId);
        message.setSubject(subject);
        message.setText(body);

        javaMailSender.send(message);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(9000) + 1000);
    }

    @Override
    public void sendUserOtp(String email) {

        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new BusinessException("User not found");

        String code = generateOtp();

        Otp otp = new Otp();
        otp.setCode(code);
        otp.setUser(user);
        otp.setUsed(false);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(2));

        otpRepository.save(otp);

        sendMail(email, "V-Serve OTP", "Your OTP is: " + code);
    }

    @Override
    public String verifyUserOtp(String email, String otpValue) {

        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new BusinessException("User not found");

        Otp otp = otpRepository.findLatestOtp(user);

        if (otp == null)
            throw new BusinessException("OTP not found");

        if (otp.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new BusinessException("OTP expired");

        if (otp.getUsed())
            throw new BusinessException("OTP already used");

        if (!otp.getCode().equals(otpValue))
            throw new BusinessException("Invalid OTP");

        otp.setUsed(true);
        otpRepository.save(otp);

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        sendMail(email,"Account Activated", "Your V-Serve account is successfully verified.");

        return "OTP verified successfully";
    }

    public void sendOrgOtp(String email) {

        Organization org = organizationRepository.findByEmail(email);

        if (org == null)
            throw new BusinessException("Organization not found");

        String code = generateOtp();

        Otp otp = new Otp();
        otp.setCode(code);
        otp.setOrganization(org);
        otp.setUsed(false);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(2));

        otpRepository.save(otp);

        sendMail(email,"V-Serve OTP",
                "Your verification OTP is: " + code);
    }

    public String verifyOrgOtp(String email, String otpValue) {

        Organization org = organizationRepository.findByEmail(email);

        Otp otp = otpRepository.findLatestOrganizationOtp(org);

        if (otp == null)
            throw new BusinessException("OTP not found");

        if (otp.getExpiryTime().isBefore(LocalDateTime.now()))
            throw new BusinessException("OTP expired");

        if (otp.getUsed())
            throw new BusinessException("OTP already used");

        if (!otp.getCode().equals(otpValue))
            throw new BusinessException("Invalid OTP");

        otp.setUsed(true);
        otpRepository.save(otp);

        org.setStatus(AccountStatus.ACTIVE);
        organizationRepository.save(org);

        sendMail(email,
                "Organization Account Activated",
                "Your organization account is verified.");

        return "Organization verified successfully";
    }

}
