package com.vserve.project.service.impl;

import com.vserve.project.dto.user.UserDashboardDto;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.service.UserDashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class UserDashboardServiceImpl implements UserDashboardService {

   private final ServiceRequestRepository requestRepository;

    public UserDashboardServiceImpl(ServiceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public UserDashboardDto getStats(Long userId, String role) {

        List<ServiceRequest> requests;

        if ("USER".equals(role)) {
            requests = requestRepository.findByRequestedBy_Id(userId);
        } else {
            requests = requestRepository.findAll();
        }

        int totalVolunteersNeeded = 0;
        int openRequests = 0;
        int upcomingRequests = 0;

        LocalDate today = LocalDate.now();

        for (ServiceRequest req : requests) {

            if (req.getMaxVolunteers() != null) {
                totalVolunteersNeeded += req.getMaxVolunteers();
            }

            if (req.getStatus() != null && req.getStatus().name().equals("OPEN")) {
                openRequests++;
            }

            if (req.getServiceDate() != null &&
                    !req.getServiceDate().isBefore(today)) {
                upcomingRequests++;
            }
        }

        return new UserDashboardDto(
                totalVolunteersNeeded,
                openRequests,
                upcomingRequests
        );
    }
}
