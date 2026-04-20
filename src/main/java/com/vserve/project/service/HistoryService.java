package com.vserve.project.service;

import com.vserve.project.dto.organization.OrganizationHistoryDto;
import com.vserve.project.dto.user.UserHistoryDto;

import java.util.List;

public interface HistoryService {

    List<UserHistoryDto> getUserHistory(Long userId);
    List<OrganizationHistoryDto> getOrganizationHistory(Long orgId);
}
