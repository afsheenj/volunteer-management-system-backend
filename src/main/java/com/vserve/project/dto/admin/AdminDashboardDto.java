package com.vserve.project.dto.admin;

import com.vserve.project.entity.DocumentVerification;
import com.vserve.project.entity.Organization;
import com.vserve.project.entity.ServiceRequest;
import com.vserve.project.entity.User;

import java.util.List;

public record AdminDashboardDto(
        long totalUsers,
        long totalOrganizations,
        long totalRequests,
        long pendingVerifications,

        List<User> recentUsers,
        List<Organization> recentOrganizations,
        List<DocumentVerification> documentSubmit,
        List<ServiceRequest> recentRequests
) {
}
