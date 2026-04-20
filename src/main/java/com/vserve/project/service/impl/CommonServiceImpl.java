package com.vserve.project.service.impl;

import com.vserve.project.dto.common.HomeContactDto;
import com.vserve.project.service.CommonService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

    private final JavaMailSender javaMailSender;

    @Value("${app.mail}")
    private String mailId;

    public CommonServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String sendContactMessage(HomeContactDto homeContactDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailId);
        message.setSubject("Home page contact form " + homeContactDto.natureOfService() );
        message.setText("Name: " + homeContactDto.fullName() +
                "\nEmail: " + homeContactDto.mailId() +
                "\nNature of Service: " + homeContactDto.natureOfService() +
                "\nDescription: " + homeContactDto.description());

        javaMailSender.send(message);

        return "Message sent successfully";
    }
}
