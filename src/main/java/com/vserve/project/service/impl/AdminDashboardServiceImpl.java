package com.vserve.project.service.impl;


import com.vserve.project.dto.admin.AdminDashboardDto;
import com.vserve.project.dto.admin.AdminServiceRequestDto;
import com.vserve.project.entity.DocumentVerification;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.User;
import com.vserve.project.enums.Category;
import com.vserve.project.enums.DocumentStatus;
import com.vserve.project.repository.DocumentVerificationRepository;
import com.vserve.project.repository.OrganizationRepository;
import com.vserve.project.repository.ServiceRequestRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.AdminDashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final UserRepository userRepository;

    public AdminDashboardServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, ServiceRequestRepository requestRepository, DocumentVerificationRepository verificationRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.requestRepository = requestRepository;
        this.verificationRepository = verificationRepository;
    }

    private final OrganizationRepository organizationRepository;
    private final ServiceRequestRepository requestRepository;
    private final DocumentVerificationRepository verificationRepository;

    @Override
    public AdminDashboardDto getDashboardData() {

        long totalUsers = userRepository.count();
        long totalOrganizations = organizationRepository.count();
        long totalRequests = requestRepository.count();
        long pendingVerifications =
                verificationRepository.countByDocumentStatus(DocumentStatus.SUBMITTED);

        List<User> recentUsers =
                userRepository.findTop6ByOrderByCreatedAtDesc();

        List<DocumentVerification> documentSubmit =
                verificationRepository.findTop3ByDocumentStatusOrderByUploadedAtDesc(DocumentStatus.SUBMITTED);

        List<Organization> recentOrganizations =
                organizationRepository.findTop6ByOrderByCreatedAtDesc();

        List<ServiceRequest> recentRequests =
                requestRepository.findTop3ByOrderByCreatedAtDesc();

        return new AdminDashboardDto(
                totalUsers,
                totalOrganizations,
                totalRequests,
                pendingVerifications,
                recentUsers,
                recentOrganizations,
                documentSubmit,
                recentRequests
        );
    }

    @Override
    public Page<AdminServiceRequestDto> getAllRequestsByAdmin(int page, int size, String category, String state, String city, String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ServiceRequest> requestPage =
                requestRepository.findWithFilters(
                        (category == null || category.isBlank()) ? null : Category.valueOf(category.toUpperCase()),
                        (state == null || state.isBlank()) ? null : state,
                        (city == null || city.isBlank()) ? null : city,
                        pageable
                );

        return requestPage.map(this::mapToAdminDto);
    }

    private AdminServiceRequestDto mapToAdminDto(ServiceRequest r) {

            String type;
            Long createdById;
            String postedBy;

            if (r.getRequestedBy() != null) {
                type = "USER";
                createdById = r.getRequestedBy().getId();
                postedBy = r.getRequestedBy().getUsername();
            } else {
                type = "ORGANIZATION";
                createdById = r.getOrganization().getId();
                postedBy = r.getOrganization().getOrgName();
            }

            return new AdminServiceRequestDto(
                    r.getId(),
                    r.getTitle(),
                    r.getDescription(),
                    r.getCategory(),
                    r.getRequestType(),

                    r.getLandmark(),
                    r.getCity(),     // ⚠️ must exist in entity
                    r.getState(),    // ⚠️ must exist in entity

                    r.getStatus().name(),

                    r.getServiceDate(),
                    r.getServiceStartTime(),
                    r.getServiceEndTime(),

                    r.getMinVolunteers(),
                    r.getMaxVolunteers(),
                    r.getRegisteredCount(),

                    type,
                    createdById,
                    postedBy,

                    r.getCreatedAt().toLocalDate()
            );
        }



}
