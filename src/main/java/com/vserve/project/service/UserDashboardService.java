package com.vserve.project.service;

import com.vserve.project.dto.user.UserDashboardDto;

public interface UserDashboardService {
    UserDashboardDto getStats(Long userId, String role);
}
