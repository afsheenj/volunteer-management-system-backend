package com.vserve.project.service;

import com.vserve.project.dto.admin.AdminDashboardDto;
import com.vserve.project.dto.admin.AdminServiceRequestDto;
import org.springframework.data.domain.Page;

public interface AdminDashboardService {
    AdminDashboardDto getDashboardData();

    Page<AdminServiceRequestDto> getAllRequestsByAdmin(int page, int size, String category, String state, String city, String sortBy, String order);
}
