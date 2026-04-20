package com.vserve.project.service.impl;

import com.vserve.project.entity.Organization;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.User;
import com.vserve.project.enums.Role;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.NotificationService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

        private final UserRepository userRepository;
        private final OrganizationRepository organizationRepository;
        private final JavaMailSender mailSender;
        private final ServiceRequestRepository serviceRequest;

        public NotificationServiceImpl(UserRepository userRepository,
                                       OrganizationRepository organizationRepository,
                                       JavaMailSender mailSender, ServiceRequestRepository serviceRequest) {
            this.userRepository = userRepository;
            this.organizationRepository = organizationRepository;
            this.mailSender = mailSender;
            this.serviceRequest = serviceRequest;
        }

    @Async
    public void sendNotificationEmails(ServiceRequest request) {

        System.out.println(" Notification method triggered");

        String state = request.getState();
        String city = request.getCity();

        List<Role> roles = List.of(Role.VOLUNTEER, Role.ORGANIZATION_MEMBER,Role.BOTH);

        List<User> users = userRepository.findUsersByLocationAndRoles(state, city, roles);

        List<Organization> orgs = organizationRepository.findOrganizationsByLocation(state, city);

        String subject = "New Service Request Available";

        String link = "http://localhost:5173/view-post/" + request.getId();

        String message = "A new service request has been created:\n\n" +
                "Title: " + request.getTitle() + "\n" +
                "Category: " + request.getCategory() + "\n" +
                "Location: " + state + ", " + city + "\n" +
                "Date: " + request.getServiceDate() + "\n\n" +
                "👉 Click here to view Post: " + link;;

        for (User user : users) {
            sendMail(user.getEmail(), subject, message);
        }

        for (Organization org : orgs) {
            sendMail(org.getEmail(), subject, message);
        }
    }

    private void sendMail(String to, String subject, String text) {
        try {
            System.out.println(" Sending mail to: " + to);
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(text);
            mailSender.send(mail);
            System.out.println(" Mail sent to: " + to);
        } catch (Exception e) {
            System.out.println("Failed to send mail to: " + to);
            e.printStackTrace();
        }
    }
}
