package com.vserve.project.service.impl;

import com.vserve.project.dto.organization.OrganizationDashboardDto;
import com.vserve.project.enums.RequestStatus;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.service.OrganizationDashboardService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class OrganizationDashboardServiceImpl implements OrganizationDashboardService {

    private final ServiceRequestRepository serviceRequestRepository;

    public OrganizationDashboardServiceImpl(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }

    @Override
    public OrganizationDashboardDto getDashboardStats(Long orgId) {

        long activeRequests =
                serviceRequestRepository.countByOrganizationIdAndStatus(orgId, RequestStatus.OPEN);

        long upcomingRequests =
                serviceRequestRepository.countByOrganizationIdAndServiceDateAfter(orgId, LocalDate.now());

        long totalVolunteersNeeded =
                serviceRequestRepository.findAll().stream()
                        .filter(req -> req.getOrganization() != null &&
                                req.getOrganization().getId().equals(orgId))
                        .mapToLong(req ->
                                (req.getMaxVolunteers() != null ? req.getMaxVolunteers() : 0)
                                        - (req.getRegisteredCount() != null ? req.getRegisteredCount() : 0)
                        )
                        .sum();

        return new OrganizationDashboardDto(
                activeRequests,
                upcomingRequests,
                totalVolunteersNeeded
        );
    }
}
