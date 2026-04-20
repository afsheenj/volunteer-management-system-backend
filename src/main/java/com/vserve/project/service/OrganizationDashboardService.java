package com.vserve.project.service;

import com.vserve.project.dto.organization.OrganizationDashboardDto;

public interface OrganizationDashboardService {
    OrganizationDashboardDto getDashboardStats(Long orgId);
}
