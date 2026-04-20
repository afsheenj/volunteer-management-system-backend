package com.vserve.project.service;

import com.vserve.project.entity.OtpDummy;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.OtpRepositoryDummy;
import com.vserve.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class DummyService {

    // Email Service
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final OtpRepositoryDummy otpRepository;

    private static final Logger log = LoggerFactory.getLogger(DummyService.class);

    public DummyService(JavaMailSender javaMailSender, UserRepository userRepository, OtpRepositoryDummy otpRepository)
    {
        testLog();
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
    }

    public void testLog() {
        System.out.println("TEST LOG");
        log.info("Test Logging");
        log.error("ERROR {}", "template value");
    }

    public void sampleMail(String mailId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailId);
        message.setSubject("Sample Mail");
        message.setText("""
               <h1>Sample Message</h1>
                """);

        javaMailSender.send(message);
    }

    public String generateOtp() {
        return String.valueOf(new Random().nextInt(9000) + 1000); // 1000 to 9999
    }

    public void sendOtp(String mail) {

        User user = userRepository.findByEmail(mail);
        if (user == null)
            throw new BusinessException("User not found");

        String code = generateOtp();

        OtpDummy otp = new OtpDummy();
        otp.setCode(code);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(2));
        otp.setUser(user);
        otp.setUsed(false);

        otpRepository.save(otp);


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mail);
        message.setSubject("Sample Mail");
        message.setText("OTP is : " + code);

        javaMailSender.send(message);
    }

    public String verifyOtp(String mail, String otp) {
        User user = userRepository.findByEmail(mail);
        if (user == null)
            throw new BusinessException("User not found");

        OtpDummy existingOtp = otpRepository.recentOtp(user);

        if (existingOtp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP is expired");
        }

        if (existingOtp.getUsed()) {
            throw new BusinessException("OTP is already used");
        }

        existingOtp.setUsed(true);
        otpRepository.save(existingOtp);

        user.setStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        if (existingOtp.getCode().equals(otp)) {
            return "OTP Verified";
        }
        return "OTP not verified";
    }
}
